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
 * ����Ʊ�ݴ�ӡxmlģ�壬�����������������ɴ�ӡָ��
 */
public class Formater {
	private Document doc; // xml�ĵ�

	public Formater(InputStream fileTemplate) {
		try {
			// ����xmlģ���ļ�
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(fileTemplate);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ��ӡ��ҳ����
	public class PrintPages {

		public int getPageCount() {
			return pages.size();
		}

		// ÿҳ������
		private ArrayList<byte[]> pages = new ArrayList<byte[]>();

		// ����һҳ
		public void addPage(byte[] page) {
			pages.add(page);
		}

		// ��õ�nҳ�����ݣ����ҳ�����ڣ�������byte[0]
		public byte[] getPage(int index) {
			if (index >= 0 && index + 1 <= pages.size()) {
				return pages.get(index);
			}
			return new byte[0];
		}
	}

	/**
	 * ��key-value����ȡ�����ݣ�����д��ģ���У���ʽ������ɴ�ӡ��ָ��
	 * 
	 * @param data
	 *            ��ֵ������
	 * @return �ɴ�ӡ��ָ��
	 */
	public PrintPages format(HashMap<String, String> mapData) {
		try {
			// ��ѯ����ҵ��Ԫ��
			NodeList tranList = doc.getElementsByTagName("Transaction");// �õ�ҵ����
			String trancode = mapData.get("trancode");// ƥ��Ľ�����

			for (int i = 0; i < tranList.getLength(); i++) {
				// �ҵ�������ƥ���Ԫ��
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
		return new PrintPages();// ���ؿ�ҳ
	}

	// ��������Ԫ��
	private PrintPages parseTransaction(Element elTran,
			HashMap<String, String> mapData) {
		PrintPages pages = new PrintPages();

		// ��������ҳ
		ArrayList<byte[]> dataList = new ArrayList<byte[]>();
		NodeList pageNL = elTran.getElementsByTagName("Page");// Pageҳ�ڵ�
		for (int i = 0; i < pageNL.getLength(); i++) {
			Element elPage = (Element) pageNL.item(i);
			dataList.clear();

			// ������ӡָ�����һ����ָ��
			NodeList cmds = elPage.getChildNodes();
			for (int j = 0; j < cmds.getLength(); j++) {
				Node node = cmds.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// ���������ָ��
					Element item = (Element) node;
					byte[] data = null;
					String name = item.getNodeName();
					if (name.equals("Raw"))// ԭʼhex����
						data = translateRaw(item);
					else if (name.equals("Span"))// �ַ���
						data = translateSpan(item, mapData);
					else if (name.equals("SpanMoney"))// ���
						data = translateSpanMoney(item, mapData);
					else if (name.equals("Money"))// ���
						data = translateMoney(item, mapData);
					else if (name.equals("Select"))// ѡ��
						data = translateSelect(item, mapData);
					else {// ��Ч��Ԫ��
					}
					if (data != null) {
						dataList.add(data);
					}
				}
			}

			// ���ɴ�ӡָ���
			byte[] res = DataFormat.ArrayList2Bytes(dataList);

			// ����ҳ
			pages.addPage(res);
		}
		return pages;
	}

	// ����RawԪ��
	private byte[] translateRaw(Element element) {
		return DataFormat.hexToBytes(element.getTextContent());
	}

	// ����Span�ı�Ԫ��
	private byte[] translateSpan(Element element, HashMap<String, String> map) {
		int len = getDataLengthAttr(element);// �����ַ�������
		int align = getDataAlignAttr(element);// ���뷽ʽ

		// �õ��������
		String value = "";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}

		byte[] content = value.getBytes(Charset.forName("GBK"));
		// ��ȫ���ÿո����
		byte[] res = new byte[len];
		for (int i = 0; i < len; i++)
			res[i] = 0x20;

		// �������
		int maxLen = content.length > len ? len : content.length;// ��������������
		if (align == ALIGN_LEFT) {
			System.arraycopy(content, 0, res, 0, maxLen);
		} else {
			System.arraycopy(content, 0, res, len - maxLen, maxLen);
		}
		return res;
	}

	// ���������
	private byte[] translateSpanMoney(Element element,
			HashMap<String, String> map) {
		int len = getDataLengthAttr(element);// �����ַ�������
		int align = getDataAlignAttr(element);// ���뷽ʽ

		// �õ��������
		String value = "";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}
		byte[] content = null;
		// ��ʽ��Ϊxx.xx����ȥ��.��
		DecimalFormat decformat = new DecimalFormat("########0.00");
		if (value.length() != 0) {
			String tmp = decformat.format(Double.parseDouble(value));
			String valueformat = "$" + tmp;
			content = valueformat.getBytes(Charset.forName("GBK"));
		}

		// ��ȫ���ÿո����
		byte[] res = new byte[len];
		for (int i = 0; i < len; i++)
			res[i] = 0x20;
		if (content != null) {
			// �������
			int maxLen = content.length > len ? len : content.length;// ��������������
			if (align == ALIGN_LEFT) {
				System.arraycopy(content, 0, res, 0, content.length);
			} else {
				System.arraycopy(content, 0, res, len - maxLen, maxLen);
			}
		}
		return res;
	}

	// ������ʽ�����
	private byte[] translateMoney(Element element, HashMap<String, String> map) {
		int len = getDataLengthAttr(element);// �����ַ�������
		int align = getDataAlignAttr(element);// ���뷽ʽ

		// �õ��������
		String value = "0";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}
		byte[] content = null;
		// ��ʽ��Ϊxx.xx����ȥ��.��
		DecimalFormat decformat = new DecimalFormat("########0.00");
		if (value.length() != 0) {
			String tmp = decformat.format(Double.parseDouble(value));
			int n = tmp.indexOf('.');
			String valueformat = "$"
					+ tmp.substring(0, n).concat(tmp.substring(n + 1));
			// $ will be changed to �� when use charset.forName("GBK);
			content = valueformat.getBytes(Charset.forName("GBK"));
		}

		// ��ȫ���ÿո����
		byte[] res = new byte[len];
		for (int i = 0; i < len; i++)
			res[i] = 0x20;
		if (content != null) {
			// �������
			int maxLen = content.length > len ? len : content.length;// ��������������
			if (align == ALIGN_LEFT) {
				System.arraycopy(content, 0, res, 0, content.length);
			} else {
				System.arraycopy(content, 0, res, len - maxLen, maxLen);
			}
		}
		return res;
	}

	// ����Selectѡ��
	private byte[] translateSelect(Element element, HashMap<String, String> map) {

		ArrayList<byte[]> resList = new ArrayList<byte[]>();

		// �õ�ѡ��ֵ
		String value = "0";
		if (element.hasAttribute("key")) {
			String key = element.getAttribute("key");
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}

		// ����Option
		NodeList optionList = element.getElementsByTagName("Option");
		for (int i = 0; i < optionList.getLength(); i++) {
			Element elOption = (Element) optionList.item(i);

			// �õ�����
			String optvalue = elOption.getAttribute("value");
			int len = getDataLengthAttr(elOption);
			int align = getDataAlignAttr(elOption);
			byte[] text = DataFormat.hexToBytes(elOption.getTextContent());
			if (elOption.hasAttribute("rawtext")) {
				String str = elOption.getAttribute("rawtext");
				if (str != null)
					text = DataFormat.hexToBytes(str);
			}

			// ��ȫ���ÿո����
			byte[] res = new byte[len];
			for (int j = 0; j < len; j++)
				res[j] = 0x20;

			// �������
			if (value.equals(optvalue)) {
				int maxLen = text.length > len ? len : text.length;// ��������������
				if (align == ALIGN_LEFT) {
					System.arraycopy(text, 0, res, 0, text.length);
				} else {
					System.arraycopy(text, 0, res, len - maxLen, maxLen);
				}
			}
			resList.add(res);
		}

		// Listת��Ϊbyte[]���
		return DataFormat.ArrayList2Bytes(resList);
	}

	// �õ���Ҫ���ַ�������
	private int getDataLengthAttr(Element element) {
		int len = 8;// Ĭ����8�ֽڳ���
		if (element.hasAttribute("length")) {
			len = Integer.parseInt(element.getAttribute("length"));
		}
		return len;
	}

	// �õ����뷽ʽ
	private final static int ALIGN_LEFT = 0; // �����
	private final static int ALIGN_RIGHT = 1; // �Ҷ���

	private int getDataAlignAttr(Element element) {
		// �ж��Ƿ����Ҷ���
		int align = ALIGN_LEFT;
		if (element.hasAttribute("align")) {
			if (element.getAttribute("align").equals("right"))
				align = ALIGN_RIGHT;
		}
		return align;
	}

}
