package org.manlier.common.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CompDocumentParser {

	private List<Record> records;
	private Pattern pattern = Pattern.compile("(\n?<[^>]*>)|(\\([^)]*\\))");

	public boolean parse(Document doc) {
		Element content_el = doc.getElementById("content");
		Element method_summary_el = content_el.getElementsByTag("table").get(1);
		Elements elements = method_summary_el.getElementsByTag("tbody").select("tr");
		records = elements.parallelStream()
				.map(element -> {
					String comp_name = element.select(".fixedFont").html();
					comp_name = pattern.matcher(comp_name).replaceAll("");
					String comp_desc = element.select(".description").text();
					return new Record(comp_name, comp_desc);
				}).collect(Collectors.toList());
		return true;
	}

	public List<Record> getRecords() {
		return records;
	}

	public static class Record {
		public final String name;
		public final String desc;

		public Record(String name, String desc) {
			this.name = name;
			this.desc = desc;
		}

		@Override
		public String toString() {
			return "Record{" +
					"name='" + name + '\'' +
					", desc='" + desc + '\'' +
					'}';
		}
	}

}
