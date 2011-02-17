package sk.seges.acris.generator.server.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sk.seges.acris.common.util.Tuple;
import sk.seges.acris.core.server.utils.io.StringFile;
import sk.seges.acris.generator.server.processor.ContentDataProvider;
import sk.seges.acris.generator.server.processor.HTMLNodeSplitter;
import sk.seges.acris.generator.server.processor.HtmlPostProcessing;
import sk.seges.acris.generator.server.processor.TokenProvider;
import sk.seges.acris.generator.server.service.persist.api.DataPersister;
import sk.seges.acris.generator.shared.domain.GeneratorToken;
import sk.seges.acris.generator.shared.domain.TokenPersistentDataProvider;
import sk.seges.acris.generator.shared.domain.api.PersistentDataProvider;
import sk.seges.acris.generator.shared.service.IGeneratorService;

/**
 * @author Peter Simun (simun@seges.sk)
 */
public class GeneratorService implements IGeneratorService {

	private static final long serialVersionUID = 6944837756691206504L;

	protected TokenProvider tokenProvider;

	protected HtmlPostProcessing htmlPostProcessing;

	private static Log log = LogFactory.getLog(GeneratorService.class);

	private DataPersister dataPersister;
	private ContentDataProvider contentDataProvider;

	private String indexFileName;

	public GeneratorService(DataPersister dataPersister, HtmlPostProcessing htmlPostProcessing, String indexFileName, TokenProvider tokenProvider,
			ContentDataProvider contentDataProvider) {
		this.dataPersister = dataPersister;
		this.indexFileName = indexFileName;
		this.htmlPostProcessing = htmlPostProcessing;
		this.tokenProvider = tokenProvider;
		this.contentDataProvider = contentDataProvider;
	}

	public GeneratorToken getLastProcessingToken() {
		return tokenProvider.getTokenForProcessing();
	}

	public boolean saveContent(GeneratorToken token, String contentText) {
		tokenProvider.setTokenForProcessing(token);
		return true;
	}

	public List<String> getAvailableNiceurls(String lang, String webId) {
		List<String> result = contentDataProvider.getAvailableNiceurls(lang, webId);

		if (log.isDebugEnabled()) {
			log.debug("Available tokens for webId: " + webId + " and language " + lang);
			for (String niceUrl : result) {
				log.debug(niceUrl);
			}
		}

		return result;
	}

	public Tuple<String, String> readHtmlBodyFromFile(String filename) {
		String content = readTextFromFile(filename);
		return new Tuple<String, String>(new HTMLNodeSplitter().getHeaderText(content), new HTMLNodeSplitter().getBody(content));
	}

	public String readTextFromFile(String filename) {

		URL url = null;

		if (filename.startsWith("http://")) {
			try {
				url = new URL(filename);
			} catch (MalformedURLException e) {
				throw new RuntimeException("Unable to load file: " + filename);
			}

			try {
				return readTextFromURL(url);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		final StringFile file = StringFile.getFileDescriptor(filename);

		try {
			return file.readTextFromFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String readTextFromURL(URL url) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

		StringBuffer content = new StringBuffer();

		String line;

		while ((line = br.readLine()) != null) {
			content.append(line);
		}

		br.close();

		return content.toString();
	}

	@Override
	public String getOfflineContentHtml(String entryPointFileName, String header, String contentWrapper, String content, GeneratorToken token,
			String currentServerURL) {

		if (content != null) {
			content = content.replaceAll("<br></br>", "<br/>");
		}

		if (log.isDebugEnabled()) {
			log.debug("Generating offline content for niceurl: " + token.getNiceUrl() + ", language: " + token.getLanguage() + " and webId: "
					+ token.getWebId());
			log.debug("			entryPointFileName: " + entryPointFileName);
			log.debug("			header: " + header);
			log.debug("			contentWrapper: " + contentWrapper);
			log.debug("			content: " + content);
			log.debug("			currentServerURL: " + currentServerURL);
		}

		String headerContent = readTextFromFile(entryPointFileName);
		String doctype = new HTMLNodeSplitter().readDoctype(headerContent);

		if (log.isDebugEnabled()) {
			log.debug("			headerContent: " + headerContent);
		}

		String entryPoint = new HTMLNodeSplitter().joinHeaders(headerContent, header);
		entryPoint = new HTMLNodeSplitter().replaceBody(entryPoint, contentWrapper);
		content = (doctype == null ? "" : ("<" + doctype + ">")) + new HTMLNodeSplitter().replaceRootContent(entryPoint, content);

		String result = content;

		if (htmlPostProcessing.setProcessorContent(content, token)) {
			result = htmlPostProcessing.getHtml();
		}

		if (token.isDefaultToken()) {
			GeneratorTokenWrapper tokenWrapper = new GeneratorTokenWrapper(token);
			tokenWrapper.setDefault(true);
			entryPoint = new HTMLNodeSplitter().joinHeaders(headerContent, header);
			entryPoint = new HTMLNodeSplitter().replaceBody(entryPoint, contentWrapper);
			content = (doctype == null ? "" : ("<" + doctype + ">")) + new HTMLNodeSplitter().replaceRootContent(entryPoint, content);

			if (htmlPostProcessing.setProcessorContent(content, tokenWrapper)) {
				writeTextToFile(htmlPostProcessing.getHtml(), true, tokenWrapper);
			}
		}

		return result;
	}

	protected String encodeFilePath(String path) {
		 try {
			path = URLEncoder.encode(path, Charset.defaultCharset().toString());
			path = path.replaceAll("%2F","/");
			path = path.replaceAll("%2f","/");
		} catch (UnsupportedEncodingException e) {
		}  

		return path;
	}
	

	protected PersistentDataProvider createPersistentDataProvider(GeneratorToken token, boolean isDefault, String content) {
		PersistentDataProvider dataProvider = new TokenPersistentDataProvider();
		dataProvider.setContent(content);
		dataProvider.setWebId(token.getWebId());
		if (isDefault) {
			dataProvider.setId(indexFileName);
		} else {
			dataProvider.setId(encodeFilePath(token.getNiceUrl()) + File.separator + "index.html");
		}
		return dataProvider;
	}

	protected void writeTextToFile(String content, boolean isDefault, GeneratorToken token) {
		if (log.isDebugEnabled()) {
			log.debug("Writing offline content for nice-url " + token.getNiceUrl() + " [ " + token.getLanguage() + " ] for " + token.getWebId());
		}

		dataPersister.writeTextToFile(createPersistentDataProvider(token, isDefault, content));
	}

	@Override
	public void writeTextToFile(String content, GeneratorToken token) {
		writeTextToFile(content, false, token);
	}
}