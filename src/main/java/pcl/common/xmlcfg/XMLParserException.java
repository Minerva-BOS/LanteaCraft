package pcl.common.xmlcfg;

public class XMLParserException extends Exception {
	public XMLParserException(String reason) {
		super(reason);
	}

	public XMLParserException(String reason, Throwable inner) {
		super(reason, inner);
	}
}