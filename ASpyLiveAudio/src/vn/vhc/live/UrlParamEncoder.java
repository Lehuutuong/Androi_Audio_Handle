package vn.vhc.live;

public class UrlParamEncoder {

	String szUrlParam = null;

	public UrlParamEncoder() {
	}

	private void prepAdd() {
	if (szUrlParam == null) {
	szUrlParam = new String();
	}
	else {
	szUrlParam += "&";
	}
	}

	public void addParam(String szName, String szValue) {
	prepAdd();
	szUrlParam += urlEncode(szName) + "=" + urlEncode(szValue);
	}

	public void addParam(String szName, int nValue) {
	prepAdd();
	szUrlParam += urlEncode(szName) + "=" + urlEncode(Integer.toString(nValue));
	}

	public void addParam(String szName, long lValue) {
	prepAdd();
	szUrlParam += urlEncode(szName) + "=" + urlEncode(Long.toString(lValue));
	}

	public void addParam(String szName, boolean bValue) {
	prepAdd();
	Boolean boolValue = new Boolean(bValue);
	szUrlParam += urlEncode(szName) + "=" + urlEncode(boolValue.toString());
	}

	private String urlEncode(String s) {
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i <=s.length()-1;i++)
	switch (s.charAt(i)) {
	case ' ':
	sb.append("%20");
	break;
	case '+':
	sb.append("%2b");
	break;
	case '\'':
	sb.append("%27");
	break;
	case '<':
	sb.append("%3c");
	break;
	case '>':
	sb.append("%3e");
	break;
	case '#':
	sb.append("%23");
	break;
	case '%':
	sb.append("%25");
	break;
	case '{':
	sb.append("%7b");
	break;
	case '}':
	sb.append("%7d");
	break;
	case '\\':
	sb.append("%5c");
	break;
	case '^':
	sb.append("%5e");
	break;
	case '~':
	sb.append("%73");
	break;
	case '[':
	sb.append("%5b");
	break;
	case ']':
	sb.append("%5d");
	break;
	default:
	sb.append(s.charAt(i));
	break;

	}
	return sb.toString();
	}

	public String toString() {
	return szUrlParam;
	}

	}