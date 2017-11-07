package tknpow22.wicketexample.app.rds;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rds<T extends IRdsContent> {

	private T item;
	private List<Rds<T>> children;

	//
	// constructor
	//

	public Rds() {
		this(null);
	}

	public Rds(T item) {
		this.item = item;
		children = new ArrayList<>();
	}

	//
	// item and children operation
	//

	public T getItem() {
		return item;
	}

	public List<Rds<T>> getChildren() {
		return children;
	}

	//
	// toString
	//

	@Override
	public String toString() {
		return toString(0);
	}

	private String toString(int indent) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%s(\n", tab(indent)));
		sb.append(String.format("%s<%s>", tab(indent+1), (item == null) ? "" : item.toString()));
		if (children.size() != 0) {
			sb.append(String.format("\n%s[\n", tab(indent+1)));
			for (Rds<T> item : children) {
				sb.append(item.toString(indent+2));
			}
			sb.append(String.format("%s]", tab(indent+1)));
		}
		sb.append(String.format("\n%s)\n", tab(indent)));

		return sb.toString();
	}

	private String tab(int tab) {
		return IntStream.range(0, tab).mapToObj(i -> "\t").collect(Collectors.joining());
	}
}
