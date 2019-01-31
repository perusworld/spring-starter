package com.yosanai.spring.starter.samplerestservice;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.yosanai.spring.starter.sampledata.Views;

import lombok.Getter;

@Getter
@JsonView(value = { Views.Public.class })
public class PagedResponse<T> {

	private int currentPage;
	private int totalPages;
	private long totalSize;
	private boolean first;
	private boolean last;
	private List<T> list;

	public PagedResponse() {
	}

	public PagedResponse(Page<T> results) {
		if (null != results) {
			list = results.getContent();
			currentPage = results.getNumber();
			totalPages = results.getTotalPages();
			totalSize = results.getTotalElements();
			first = results.isFirst();
			last = results.isLast();
		}
	}

	@JsonIgnore
	public boolean isEmpty() {
		return null == list || list.isEmpty();
	}

	@JsonIgnore
	public Stream<T> stream() {
		return null == list ? null : list.stream();
	}

	@JsonIgnore
	public int size() {
		return null == list ? 0 : list.size();
	}

}
