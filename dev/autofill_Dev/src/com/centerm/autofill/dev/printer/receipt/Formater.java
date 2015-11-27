package com.centerm.autofill.dev.printer.receipt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.centerm.util.DataFormat;

/**
 * 解析票据打印xml模板，并根据请求数据生成打印指令
 */
public class Formater {
	private Document doc; // xml文档

	public Formater(InputStream fileTemplate) {
		try {
			// 解析xml模板文件
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(fileTemplate);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 打印的页数据
	public class PrintPages {

		public int getPageCount() {
			return pages.size();
		}

		// 每页的内容
		private ArrayList<byte[]> pages = new ArrayList<byte[]>();

		// 增加一页
		public void addPage(byte[] page) {
			pages.add(page);
		}

		// 获得第n页的内容，如果页不存在，将返回byte[0]
		public byte[] getPage(int index) {
			if (index >= 0 && index + 1 <= pages.size()) {
				return pages.get(index);
			}
			return new byte[0];
		}
	}

	/**
	 * 从key-value表中取出数据，并填写到模板中，格式化输出可打印的指令
	 * 
	 * @param data
	 *            键值对数据
	 * @return 可打印的指令
	 */
	public PrintPages format(HashMap<String, String> mapData) {
		try {
			// 轮询各个业务元素
			NodeList tranList = doc.getElementsByTagName("Transaction");// 得到业务结点
			String trancode = mapData.get("trancode");// 匹配的交易码

			for (int i = 0; i < tranList.getLength(); i++) {
				// 找到交易码匹配的元素
				Element element = (Element) tranList.item(i);
				if (element.getNodeName().equals("Transaction")) {
					Log.i("Transaction:", element.getNodeName());
					String[] codes = element.getAttribute("code").split("\\^");
					for (int j = 0; j < codes.length; j++) {
						Log.i("codes:", codes[j]);
						if (codes[j].equals(trancode))
							return parseTransaction(element, mapData);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new PrintPages();// 返回空页
	}

	// 解析交易元素
	private PrintPages parseTransaction(Element elTran,
			HashMap<String, String> mapData) {
		PrintPages pages = new PrintPages();

		// 遍历各个页
		ArrayList<byte[]> dataList = new ArrayList<byte[]>();
		NodeList pageNL = elTran.getElementsByTagName("Page");// Page页节点
		for (int i = 0; i < pageNL.getLength(); i++) {
			Element elPage = (Element) pageNL.item(i);
			dataList.clear();

			// 遍历打印指令项，逐一生成指令
			NodeList cmds = elPage.getChildNodes();
			for (int j = 0; j < cmds.getLength(); j++) {
				Node node = cmds.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// 翻译出单条指令
					Element item = (Element) node;
					byte[] data = null;
					String name = item.getNodeName();
					if (name.equals("Raw"))// 原始hex命令
						data = translateRaw(item);
					else if (name.equals("Span"))// 字符串
						data = translateSpan(item, mapData);
					else if (name.equals("SpanMoney"))// 金额
						data = translateSpanMoney(item, mapData);
					else if (name.equals("Money"))// 金额
						data = translateMoney(item, mapData);
					else if (name.equals("Select"))// 选项
						data = translateSelect(item, mapData);
					else {// 无效的元素
					}
					if (data != null) {
						dataList.add(data);
					}
				}
			}

			// 生成打印指令包
			byte[] res = DataFormat.ArrayList2Bytes(dataList);

			// 加入页
			pages.addPage(res);
		}
		return pages;
	}

	// 解析Raw元素
	private byte[] translateRaw(Element element) {
		return DataFormat.hexToBytes(element.getTextContent());
	}

	// 解析Span文本元素
	private byte[] translateSpan(Element element, HashMap<String, String> map) {
		int len = getDataLengthAttr(element);// 计算字符串长度
		int align = getDataAlignAttr(element);// 对齐方式

		// 得到填充内容
		String value = "";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}

		byte[] content = value.getBytes(Charset.forName("GBK"));
		// 先全部用空格填充
		byte[] res = new byte[len];
		for (int i = 0; i < len; i++)
			res[i] = 0x20;

		// 填充内容
		int maxLen = content.length > len ? len : content.length;// 避免数据量过大
		if (align == ALIGN_LEFT) {
			System.arraycopy(content, 0, res, 0, maxLen);
		} else {
			System.arraycopy(content, 0, res, len - maxLen, maxLen);
		}
		return res;
	}

	// 解析填充金额
	private byte[] translateSpanMoney(Element element,
			HashMap<String, String> map) {
		int len = getDataLengthAttr(element);// 计算字符串长度
		int align = getDataAlignAttr(element);// 对齐方式

		// 得到填充内容
		String value = "";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}
		byte[] content = null;
		// 格式化为xx.xx，并去除.号
		DecimalFormat decformat = new DecimalFormat("########0.00");
		if (value.length() != 0) {
			String tmp = decformat.format(Double.parseDouble(value));
			String valueformat = "$" + tmp;
			content = valueformat.getBytes(Charset.forName("GBK"));
		}

		// 先全部用空格填充
		byte[] res = new byte[len];
		for (int i = 0; i < len; i++)
			res[i] = 0x20;
		if (content != null) {
			// 填充内容
			int maxLen = content.length > len ? len : content.length;// 避免数据量过大
			if (align == ALIGN_LEFT) {
				System.arraycopy(content, 0, res, 0, content.length);
			} else {
				System.arraycopy(content, 0, res, len - maxLen, maxLen);
			}
		}
		return res;
	}

	// 解析格式化金额
	private byte[] translateMoney(Element element, HashMap<String, String> map) {
		int len = getDataLengthAttr(element);// 计算字符串长度
		int align = getDataAlignAttr(element);// 对齐方式

		// 得到填充内容
		String value = "0";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}
		byte[] content = null;
		// 格式化为xx.xx，并去除.号
		DecimalFormat decformat = new DecimalFormat("########0.00");
		if (value.length() != 0) {
			String tmp = decformat.format(Double.parseDouble(value));
			int n = tmp.indexOf('.');
			String valueformat = "$"
					+ tmp.substring(0, n).concat(tmp.substring(n + 1));
			// $ will be changed to ￥ when use charset.forName("GBK);
			content = valueformat.getBytes(Charset.forName("GBK"));
		}

		// 先全部用空格填充
		byte[] res = new byte[len];
		for (int i = 0; i < len; i++)
			res[i] = 0x20;
		if (content != null) {
			// 填充内容
			int maxLen = content.length > len ? len : content.length;// 避免数据量过大
			if (align == ALIGN_LEFT) {
				System.arraycopy(content, 0, res, 0, content.length);
			} else {
				System.arraycopy(content, 0, res, len - maxLen, maxLen);
			}
		}
		return res;
	}

	// 解析Select选项
	private byte[] translateSelect(Element element, HashMap<String, String> map) {

		ArrayList<byte[]> resList = new ArrayList<byte[]>();

		// 得到选项值
		String value = "0";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}

		// 遍历Option
		NodeList optionList = element.getElementsByTagName("Option");
		for (int i = 0; i < optionList.getLength(); i++) {
			Element elOption = (Element) optionList.item(i);

			// 得到属性
			String optvalue = elOption.getAttribute("value");
			int len = getDataLengthAttr(elOption);
			int align = getDataAlignAttr(elOption);
			byte[] text = DataFormat.hexToBytes(elOption.getTextContent());
			if (elOption.hasAttribute("rawtext")) {
				String str = elOption.getAttribute("rawtext");
				if (str != null)
					text = DataFormat.hexToBytes(str);
			}

			// 先全部用空格填充
			byte[] res = new byte[len];
			for (int j = 0; j < len; j++)
				res[j] = 0x20;

			// 填充内容
			if (value.equals(optvalue)) {
				int maxLen = text.length > len ? len : text.length;// 避免数据量过大
				if (align == ALIGN_LEFT) {
					System.arraycopy(text, 0, res, 0, text.length);
				} else {
					System.arraycopy(text, 0, res, len - maxLen, maxLen);
				}
			}
			resList.add(res);
		}

		// List转化为byte[]输出
		return DataFormat.ArrayList2Bytes(resList);
	}

	// 得到需要的字符串长度
	private int getDataLengthAttr(Element element) {
		int len = 8;// 默认是8字节长度
		if (element.hasAttribute("length")) {
			len = Integer.parseInt(element.getAttribute("length"));
		}
		return len;
	}

	// 得到对齐方式
	private final static int ALIGN_LEFT = 0; // 左对齐
	private final static int ALIGN_RIGHT = 1; // 右对齐

	private int getDataAlignAttr(Element element) {
		// 判断是否左、右对齐
		int align = ALIGN_LEFT;
		if (element.hasAttribute("align")) {
			if (element.getAttribute("align").equals("right"))
				align = ALIGN_RIGHT;
		}
		return align;
	}

}
