package com.yosanai.spring.starter.samplerestservice.docs;

import static org.junit.Assert.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yosanai.spring.starter.samplerestservice.PagedResponse;

public class BaseAsciiDocTest {
	public static final String END = "end";

	public static final String START = "start";

	public static final String REPORT_TYPE = "reportType";

	public static final String DIR = "dir";

	public static final String DESC = "desc";

	public static final String ASC = "asc";

	public static final String SORT = "sort";

	public static final int INSERT_SIZE = 3;

	public static final String OAUTH_2_TOKEN = "Bearer oauth-2-token-from-auth-service";

	public static final String AUTHORIZATION_HEADER = "Authorization";

	public static final String SIZE = "size";

	public static final String PAGE = "page";

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	WebApplicationContext context;

	protected MockMvc mockMvc;

	@Value("${server.port}")
	protected int serverPort;

	@Autowired
	protected ObjectMapper objectMapper;

	private RestDocumentationResultHandler document;

	@PersistenceContext
	EntityManager entityManager;

	protected String rndPhone() {
		return RandomStringUtils.random(10, false, true);
	}

	protected String getPath(Class<?> classDef, String... path) {
		return String.format("%s/%s", getRequestRootPath(classDef),
				null == path ? "" : Stream.of(path).collect(Collectors.joining("/")));
	}

	public String getRequestRootPath(Class<?> classDef) {
		return classDef.getAnnotation(RequestMapping.class).value()[0];
	}

	protected String encodeAsParams(boolean encode, Object... params) {
		StringBuilder ret = new StringBuilder();
		if (null != params) {
			for (int idx = 0; idx < params.length; idx = idx + 2) {
				if (0 < ret.length()) {
					ret.append("&");
				}
				try {
					ret.append(params[idx]).append("=")
							.append(encode
									? URLEncoder.encode(params[idx + 1].toString(), StandardCharsets.UTF_8.toString())
									: params[idx + 1].toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		}
		return ret.toString();
	}

	protected String getURLWithParam(String url, Object... params) {
		return StringUtils.joinWith("?", url, encodeAsParams(false, params));
	}

	public <T> T create(Class<?> classDef, Class<T> response, T obj, boolean genDoc) {
		T ret = null;
		String url = getPath(classDef, "");
		try {
			MockHttpServletRequestBuilder post = post(url).header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN)
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(obj));
			MvcResult result = mockMvc.perform(post).andDo(genDoc ? this.document : print()).andExpect(status().isOk())
					.andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			Long id = (Long) MethodUtils.invokeMethod(ret, "getId");
			assertNotNull(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> T postTo(Class<?> classDef, Class<T> response, Object obj, boolean genDoc, String... paths) {
		T ret = null;
		String url = getPath(classDef, paths);
		try {
			MockHttpServletRequestBuilder post = post(url).header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN)
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(obj));
			MvcResult result = mockMvc.perform(post).andDo(genDoc ? this.document : print()).andExpect(status().isOk())
					.andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> T postToWithoutHeader(Class<?> classDef, Class<T> response, Object obj, boolean genDoc,
			String... paths) {
		T ret = null;
		String url = getPath(classDef, paths);
		try {
			MockHttpServletRequestBuilder post = post(url).contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(obj));
			MvcResult result = mockMvc.perform(post).andDo(genDoc ? this.document : print()).andExpect(status().isOk())
					.andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> T postTo(Class<?> classDef, TypeReference<T> response, Object obj, boolean genDoc, String... paths) {
		T ret = null;
		String url = getPath(classDef, paths);
		try {
			MockHttpServletRequestBuilder post = post(url).header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN)
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(obj));
			MvcResult result = mockMvc.perform(post).andDo(genDoc ? this.document : print()).andExpect(status().isOk())
					.andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T, ID> T updateBy(Class<?> classDef, Class<T> response, Object obj, boolean genDoc, String path,
			Object... pathArgs) {
		T ret = null;
		String url = getPath(classDef, path);
		boolean useDoc = genDoc && null != pathArgs && 0 < pathArgs.length;
		try {
			MockHttpServletRequestBuilder put = useDoc ? RestDocumentationRequestBuilders.put(url, pathArgs)
					: put(url, pathArgs).header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN)
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(obj));
			MvcResult result = mockMvc.perform(put).andDo(genDoc ? this.document : print()).andExpect(status().isOk())
					.andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			Long id = (Long) MethodUtils.invokeMethod(ret, "getId");
			assertNotNull(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return ret;
	}

	public <T, ID> T deleteBy(Class<?> classDef, Class<T> response, Object obj, boolean genDoc, String path,
			Object... pathArgs) {
		T ret = null;
		String url = getPath(classDef, path);
		boolean useDoc = genDoc && null != pathArgs && 0 < pathArgs.length;
		try {
			MockHttpServletRequestBuilder delete = useDoc ? RestDocumentationRequestBuilders.delete(url, pathArgs)
					: delete(url, pathArgs).header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN)
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(obj));
			MvcResult result = mockMvc.perform(delete).andDo(genDoc ? this.document : print())
					.andExpect(status().isOk()).andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			Long id = (Long) MethodUtils.invokeMethod(ret, "getId");
			assertNotNull(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> PagedResponse<T> pagedResponse(Class<?> classDef, Class<T> response, boolean genDoc, Object... args) {
		PagedResponse<T> ret = null;
		MockHttpServletRequestBuilder requestBuilder = get(getURLWithParam(getPath(classDef, ""), args))
				.header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN).accept(MediaType.APPLICATION_JSON);
		try {
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(),
					objectMapper.getTypeFactory().constructParametricType(PagedResponse.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> PagedResponse<T> pagedResponseWithPath(Class<?> classDef, Class<T> response, boolean genDoc, String path,
			Object... args) {
		PagedResponse<T> ret = null;
		MockHttpServletRequestBuilder requestBuilder = get(getURLWithParam(getPath(classDef, path), args))
				.header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN).accept(MediaType.APPLICATION_JSON);
		try {
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(),
					objectMapper.getTypeFactory().constructParametricType(PagedResponse.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> PagedResponse<T> pagedResponseWithSort(Class<?> classDef, Class<T> response, String sortField,
			boolean ascending, boolean genDoc, Object... args) {
		PagedResponse<T> ret = null;
		MockHttpServletRequestBuilder requestBuilder = get(getURLWithParam(getPath(classDef, ""), args)
				+ (null == sortField ? "" : "&" + SORT + "=" + String.join(",", sortField, ascending ? ASC : DESC)))
						.header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN).accept(MediaType.APPLICATION_JSON);
		try {
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(),
					objectMapper.getTypeFactory().constructParametricType(PagedResponse.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public Object[] templateToKeyValue(String template, Object... args) {
		Object[] ret = null;
		List<Object> entries = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(template);
		int count = 0;
		while (matcher.find()) {
			entries.add(matcher.group(1));
			entries.add(args[count++]);
		}
		ret = entries.toArray(new Object[] {});
		return ret;
	}

	public <T> T findBy(Class<?> classDef, Class<T> response, boolean genDoc, String path, Object... pathArgs) {
		T ret = null;
		String url = getPath(classDef, path);
		boolean useDoc = genDoc && null != pathArgs && 0 < pathArgs.length;
		MockHttpServletRequestBuilder requestBuilder = (useDoc ? RestDocumentationRequestBuilders.get(url, pathArgs)
				: get(url, pathArgs)).header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN).accept(MediaType.APPLICATION_JSON);
		try {
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			ret = objectMapper.readValue(result.getResponse().getContentAsString(), response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;
	}

	public <T> List<T> findAllBy(Class<?> classDef, Class<T> response, boolean genDoc, String path,
			Object... pathArgs) {
		List<T> ret = null;
		try {
			String url = getPath(classDef, path);
			boolean useDoc = genDoc && null != pathArgs && 0 < pathArgs.length;
			MockHttpServletRequestBuilder requestBuilder = (useDoc ? RestDocumentationRequestBuilders.get(url, pathArgs)
					: get(url, pathArgs)).header(AUTHORIZATION_HEADER, OAUTH_2_TOKEN)
							.accept(MediaType.APPLICATION_JSON);
			MvcResult result = mockMvc.perform(requestBuilder).andDo(genDoc ? document : print())
					.andExpect(status().isOk()).andReturn();
			String contentAsString = result.getResponse().getContentAsString();
			if (StringUtils.isEmpty(contentAsString)) {
				contentAsString = "[]";
			}
			ret = objectMapper.readValue(contentAsString,
					objectMapper.getTypeFactory().constructCollectionType(List.class, response));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return ret;

	}

	public void setupDocument(Snippet... snippets) {
		this.document = MockMvcRestDocumentation.document("{method-name}",
				Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()), snippets);
	}

	public List<ParameterDescriptor> add(List<ParameterDescriptor> params, String... args) {
		for (int idx = 0; idx < args.length; idx += 2) {
			params.add(RequestDocumentation.parameterWithName(args[idx]).description(args[idx + 1]));
		}
		return params;
	}

	public Snippet requestParameters(String... args) {
		return RequestDocumentation.requestParameters(add(new ArrayList<>(), args));
	}

	public Snippet pathParameters(String... args) {
		return RequestDocumentation.pathParameters(add(new ArrayList<>(), args));
	}

	public Snippet requestFilterAndSortParameters(String sortField, String... filterFields) {

		List<ParameterDescriptor> params = new ArrayList<>();
//		add(params, PAGE, "page to get", SIZE, "number of results per page", "sort",
//				String.format("example sort field in object to sort by(%s), sort direction (asc, desc)", sortField));
//		for (String filterField : filterFields) {
//			add(params, filterField, String.format(FILTER_FIELD_DESC, filterField));
//		}
		return RequestDocumentation.requestParameters(params);
	}

	public RestDocumentationResultHandler defaultDocument(ResponseFieldsSnippet resp, RequestFieldsSnippet req) {
		return MockMvcRestDocumentation.document("{method-name}",
				Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()), resp, req);
	}

	public RestDocumentationResultHandler defaultDocument(Snippet... snippets) {
		return MockMvcRestDocumentation.document("{method-name}",
				Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()), snippets);
	}

	public FieldDescriptor[] fieldDescriptorsFor(String... strings) {
		List<FieldDescriptor> ret = new ArrayList<>();
		for (int idx = 0; idx < strings.length; idx += 2) {
			ret.add(fieldWithPath(strings[idx]).description(strings[idx + 1]));
		}
		return ret.toArray(new FieldDescriptor[] {});
	}

	@Before
	public void setUp() {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		setupDocument();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).apply(
				MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation).uris().withPort(serverPort))
				.build();
	}

}
