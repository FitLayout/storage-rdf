package org.fit.layout.storage.example;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * it is one thread link crawler for one specific seed (url)
 * 
 * @author milicka
 * @version 0.9
 */
public class LinkCrawler {

	private List<String> crawled = new ArrayList<String>();
	private List<String> toCrawl = new ArrayList<String>();
	private String baseUrl;

	private String actualCrawlUrl;
	private int LINK_LIMIT = 100;
	private final Boolean ADD_ONLY_VALID_URLS = false; // it adds url when is
														// correctly downloaded

	// source https://code.google.com/p/crawler4j/
	private final Pattern BINARY_FILES_EXTENSIONS = Pattern
			.compile(".*\\.(bmp|gif|jpe?g|png|tiff?|pdf|ico|xaml|pict|rif|pptx?|ps"
					+ "|mid|mp2|mp3|mp4|wav|wma|au|aiff|flac|ogg|3gp|aac|amr|au|vox"
					+ "|avi|mov|mpe?g|ra?m|m4v|smil|wm?v|swf|aaf|asf|flv|mkv"
					+ "|zip|rar|gz|7z|aac|ace|alz|apk|arc|arj|dmg|jar|lzip|lha)"
					+ "(\\?.*)?$"); // For url Query parts ( URL?q=... )

	/**
	 * Initialization of link crawler
	 */
	public LinkCrawler() {
		super();
	}

	/**
	 * Appends seed into list of searching domains
	 * 
	 * @param newSeed
	 *            contains domain name where crawling starts
	 */
	public void setSeed(String newSeed) {
		baseUrl = getBaseUrl(newSeed);

		toCrawl.clear();
		toCrawl.add(baseUrl);
	}

	/**
	 * Launches links downloading
	 */
	public void start() {

		while (toCrawl.size() > 0) {
			try {
				runNextUrlCrawling();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LinkLimit e) {
				break;
			}
		}
	}

	/**
	 * size of crawled output list
	 * 
	 * @return
	 */
	public int size() {
		return crawled.size();
	}

	/**
	 * Checks the link existance in the output list
	 * 
	 * @param str
	 *            controlled link
	 * @return existence of the url in the crawling list
	 */
	public Boolean contains(String str) {
		return crawled.contains(str);
	}

	/**
	 * Gets url from the crawled list on the specific index
	 * 
	 * @param index
	 * @return url on the specific index
	 */
	public String get(int index) {
		return crawled.get(index);
	}

	/**
	 * sets maximum number of urls
	 * 
	 * @param newLimit
	 */
	public void setLinksLimit(int newLimit) {
		LINK_LIMIT = newLimit;
	}

	/**
	 * Gets list of crawled urls
	 * @return
	 */
	public List<String> getUrls() {
		return crawled;
	}
	
	// =============================================================================================================

	/**
	 * Takes next url from toCrawl list and does link crawling
	 * 
	 * @throws IOException
	 *             general exception
	 * @throws LinkLimit
	 *             in case of links limit is exhausted
	 */
	private void runNextUrlCrawling() throws IOException, LinkLimit {

		// list is empty
		if (toCrawl.size() == 0) {
			return;
		}

		// sets actual url and removes it from the waiting list
		actualCrawlUrl = toCrawl.get(0).toString();
		toCrawl.remove(0);

		// downloading document content
		URL url = new URL(actualCrawlUrl);
		String actualPage = loadURL2String(url);

		// document parsing for the link crawling
		Document doc = null;
		doc = Jsoup.parse(actualPage);
		Elements links = doc.select("a"); // a with href

		// it can be added if the content was valid
		this.add(actualCrawlUrl);

		// iterates over all links
		ListIterator<Element> li = links.listIterator();
		while (li.hasNext()) {
			Element e = li.next();
			appendUrlToCrawlIfDoesNotExist(e.attr("href"));
		}

	}

	/**
	 * Appends url into crawling list if it passes conditions
	 * 
	 * @param url
	 * @throws LinkLimit
	 *             the limit of links was exceeded
	 */
	private void appendUrlToCrawlIfDoesNotExist(String url) throws LinkLimit {

		url = url.toLowerCase();

		// is there invalid extension
		if (BINARY_FILES_EXTENSIONS.matcher(url).matches())
			return;

		// check limit of allowed links
		if (size() > LINK_LIMIT && LINK_LIMIT > 0)
			throw new LinkLimit("Limit exceeded " + LINK_LIMIT);

		url = normalizeUrl(url);

		if (this.toCrawl.contains(url) || this.contains(url)
				|| !url.startsWith(baseUrl) || url.startsWith("mailto:")
				|| url.startsWith("file:") || url.startsWith("file:")) {
			return;
		} else {

			try {
				URL urlTest = new URL(url); // url validation
				urlTest.openConnection();
				toCrawl.add(url);

				if (!ADD_ONLY_VALID_URLS)
					this.add(url); // it adds url when is seen it does not
									// guaranteed validity
			} catch (Exception ex) {
				System.err.println("Nepridano URL " + url);
			}
		}
	}

	/**
	 * Appends only new string
	 * 
	 * @param str
	 */
	private boolean add(String str) {
		if (!contains(str)) {
			crawled.add(str);
			return true;
		} else
			return false;
	}

	/**
	 * Normalizes url depending on the starts (especially ./, ?, #,...)
	 * 
	 * @param oldUrl
	 * @return normalized url
	 */
	private String normalizeUrl(String oldUrl) {

		String url = oldUrl.toLowerCase();
		if (oldUrl.startsWith("./")) { // href="./next-page"
			url = baseUrl + url.substring(1);
		} else if (oldUrl.startsWith("../")) {// href="../next-page"
			url = baseUrl + url.substring(1);
		} else if (oldUrl.startsWith("/")) { // href="/next-page"
			url = baseUrl + oldUrl;
		} else if (oldUrl.startsWith("?")) { // href="?hash=dasdasd"
			url = actualCrawlUrl + oldUrl;
		} else if (oldUrl.startsWith("#")) { // href="#next-page"
			url = oldUrl.substring(0, oldUrl.indexOf("#"));
		} else if (!oldUrl.startsWith("http:") && !oldUrl.startsWith("https:")
				&& !oldUrl.startsWith("file:") && !oldUrl.startsWith("mailto:")) { //
			url = getDirBasedUrl(actualCrawlUrl) + oldUrl;
		}

		return url;
	}

	/**
	 * Appends splash to url
	 * 
	 * @param url
	 *            for appending splash
	 * @return url wiht splash
	 */
	private String getDirBasedUrl(String url) {
		return getBaseUrl(url) + "/";
	}

	/**
	 * Url nomalization to be without / in the end
	 * 
	 * @param url
	 *            for normalization
	 * @return normalized url string
	 */
	private String getBaseUrl(String url) {

		if (url.endsWith("/")) {
			// removing '/' in the end
			url = url.substring(0, url.length() - 1);
		}

		return url;
	}

	/**
	 * Loads page from URL
	 * 
	 * @param url
	 *            downloaded page
	 * @return string content
	 * @throws IOException
	 */
	private String loadURL2String(URL url) throws IOException {

		URLConnection con = url.openConnection();

		Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
		Matcher m = p.matcher(con.getContentType());
		/*
		 * If Content-Type doesn't match this pre-conception, choose default and
		 * hope for the best.
		 */
		String charset = m.matches() ? m.group(1) : "ISO-8859-1";
		Reader r = new InputStreamReader(con.getInputStream(), charset);
		StringBuilder buf = new StringBuilder();
		while (true) {
			int ch = r.read();
			if (ch < 0)
				break;
			buf.append((char) ch);
		}

		return buf.toString();
	}

	// =============================================================================================================

	/**
	 * Exception for maximum link limit
	 * 
	 * @author milicka
	 * 
	 */
	class LinkLimit extends Exception {
		private static final long serialVersionUID = 1L;

		LinkLimit(String string) {
			super(string);
		}
	}

}
