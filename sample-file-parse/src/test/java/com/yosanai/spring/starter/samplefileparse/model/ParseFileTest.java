package com.yosanai.spring.starter.samplefileparse.model;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.beanio.BeanReader;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@Slf4j
public class ParseFileTest {
	@Value("classpath:parser/simple.xml")
	Resource mapping;

	@Value("classpath:files/test.txt")
	Resource input;

	@Test
	public void readTest() throws Exception {
		StreamFactory factory = StreamFactory.newInstance();
		factory.load(mapping.getInputStream());
		BeanReader in = factory.createReader("outgoingFile", new InputStreamReader(input.getInputStream()));
		BaseRecord obj;
		List<BaseRecord> entries = new ArrayList<>();
		while ((obj = (BaseRecord) in.read()) != null) {
			entries.add(obj);
			log.debug(obj.toString());
		}
		in.close();
		StringWriter ops = new StringWriter();
		BeanWriter writer = factory.createWriter("outgoingFile", ops);
		for (Object object : entries) {
			writer.write(object);
		}
		writer.flush();
		writer.close();
		String srcString = FileUtils.readFileToString(input.getFile(), "UTF-8");
		String genString = ops.toString();
		assertEquals(srcString, genString);
		log.debug(srcString);
		log.debug(genString);
	}

}
