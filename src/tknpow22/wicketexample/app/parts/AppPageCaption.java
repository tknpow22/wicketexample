package tknpow22.wicketexample.app.parts;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * アプリケーションで使用するページキャプションを定義する。
 */
public class AppPageCaption extends Panel {

	public AppPageCaption(String id, String caption) {
		super(id, new Model<String>(caption));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new Label("parts-caption", getDefaultModelObjectAsString()));
	}
}
