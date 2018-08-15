package cn.emay.rocketmq;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.emay.office.excel.ExcelReader;
import cn.emay.office.excel.common.ExcelCellType;
import cn.emay.office.excel.common.ExcelVersion;
import cn.emay.office.excel.read.ExcelReadHandler;
import cn.emay.office.excel.read.ExcelSheetReadHandler;
import cn.emay.office.excel.read.impl.ExcelReadHandlerImpl;
import cn.emay.office.excel.read.impl.ExcelSimpleSheeetReadHandlerImpl;
import cn.emay.office.excel.schema.ExcelColumnSchema;
import cn.emay.office.excel.schema.ExcelSchema;
import cn.emay.office.excel.schema.ExcelSheetSchema;
import cn.emay.office.excel.schema.impl.ExcelColumnSchemaImpl;
import cn.emay.office.excel.schema.impl.ExcelSchemaImpl;
import cn.emay.office.excel.schema.impl.ExcelSheetSchemaImpl;
import cn.emay.utils.number.BigDecimalUtils;

public class TestEx {

	public static void main(String[] args) {
		ExcelColumnSchema column = new ExcelColumnSchemaImpl(ExcelCellType.STRING, 2);
		ExcelColumnSchema column2 = new ExcelColumnSchemaImpl(ExcelCellType.DOUBLE, 3);
		ExcelSheetSchema sheet = new ExcelSheetSchemaImpl(0, new ExcelColumnSchema[] { column,column2 });
		ExcelSchema scheam = new ExcelSchemaImpl(ExcelVersion.Excel07, sheet);
		File file = new File("C:\\Users\\Frank\\Desktop\\员工评价");
		
		Map<String,String> everyTil = new HashMap<>();//每个人得到的评价
		Map<String,String> everyme = new HashMap<>();//每个人输出的评价
		
		Map<String,Set<Double>> everyt = new HashMap<>();//每个人输出的评分档次
		Map<String,List<Double>> everyt1 = new HashMap<>();//每个人输出获得的评分
		
		for(File fi : file.listFiles()) {
			ExcelSimpleSheeetReadHandlerImpl sheet1 = new ExcelSimpleSheeetReadHandlerImpl(0, 1);
			ExcelReadHandler handler = new ExcelReadHandlerImpl(new ExcelSheetReadHandler[] { sheet1 });
			ExcelReader.readExcel(fi.getAbsolutePath(), scheam, handler);
			List<Object[]> list = sheet1.getReadDatas();
			
			String me = null;
			String you = null;
			double count = 0d;
			String tit = "";
			
			for (int i = 0 ; i < 12 ; i ++) {
				Object[] obj = list.get(i);
				if(i == 0) {
					you = (String) obj[0];
					continue;
				}else if(i == 1) {
					continue;
				}else if(i == 11) {
					me = (String) obj[0];
					continue;
				}
				
				if(i == 2 || i == 8 || i == 5) {
					count += (double) obj[1];
				}
				
				if(obj[0] != null) {
					tit += (String)obj[0] + ",";
				}
			}
			
			if(me.contains("名") || me.contains("者") ) {
				System.out.println(fi.getAbsolutePath());
				System.exit(1);
			}
			if(you.contains("名") || you.contains("者") ) {
				System.out.println(fi.getAbsolutePath());
				System.exit(1);
			}
			
			you = you.equals("何玉华") ? "何于华" : you;
			you = you.equals("刘杰.") ? "刘杰" : you;
			you = you.equals("崔惠芬") ? "崔慧芬" : you;
			you = you.equals("王燕军") ? "王艳军" : you;
			you = you.equals("于德军") ? "余德军" : you;
			
			
			count = BigDecimalUtils.div(new BigDecimal(count), new BigDecimal(1),2).doubleValue();
			
			if(!everyt.containsKey(me)) {
				everyt.put(me, new LinkedHashSet<>());
			}
			everyt.get(me).add(count);
			
			if(!everyt1.containsKey(you)) {
				everyt1.put(you, new ArrayList<>());
			}
			everyt1.get(you).add(count);
			
			
//			System.out.println("me\t" + me);
//			System.out.println("you\t" + you);
//			System.out.println("count\t" + count );
//			System.out.println("tit\t" + tit);
//			
			
			if(everyTil.containsKey(you)) {
				everyTil.put(you, everyTil.get(you) + tit);
			}else {
				everyTil.put(you, tit);
			}
			
			if(everyme.containsKey(me)) {
				everyme.put(me, everyme.get(me) + tit);
			}else {
				everyme.put(me, tit);
			}
			
//			System.out.println();
		}
		
		System.out.println("# 一、每个人得到的评价");
		for(String key : everyTil.keySet()) {
			System.out.println("## "+ key + "\n" + everyTil.get(key));
		}
		System.out.println("# 二、每个人输出的评价");
		for(String key : everyme.keySet()) {
			System.out.println("## "+ key + "\n" + everyme.get(key));
		}
		System.out.println("# 三、每个人输出的评分档次");
		for(String key : everyt.keySet()) {
			System.out.println("## "+ key + "\n" + everyt.get(key).toString().replace("[", "").replace("]", "").replace(",", " , "));
		}
		System.out.println("# 四、每个人获得的所有评分");
		for(String key : everyt1.keySet()) {
			System.out.println("## "+ key + "\n" + everyt1.get(key).toString().replace("[", "").replace("]", "").replace(",", " , "));
		}
		System.out.println("# 五、每个人的评分");
		for(String key : everyt1.keySet()) {
			double tto = 0d;
			for(double d : everyt1.get(key)) {
				tto += d;
			}
			tto = BigDecimalUtils.div(new BigDecimal(tto/everyt1.get(key).size()), new BigDecimal(1),2).doubleValue();
			System.out.println("## "+ key + "\n" + tto);
		}

	}

}
