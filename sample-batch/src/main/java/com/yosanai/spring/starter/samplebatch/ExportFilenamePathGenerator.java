package com.yosanai.spring.starter.samplebatch;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExportFilenamePathGenerator {

	@Value("${samplebatch.step.output.path}")
	private String path;

	@Value("${samplebatch.step.output.fileName}")
	private String fileNameFormat;

	public String getNextPath(int index) {
		return getNextPath(index, new Date());
	}

	public String getNextPath(int index, Date date) {
		return new File(path, String.format(fileNameFormat, date, index)).getAbsolutePath();
	}
}
