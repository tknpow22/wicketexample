package tknpow22.wicketexample.app.parts;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * アプリケーションで使用する Bootstrap 用の FeedbackPanel を定義する。
 */
public class AppFeedbackPanel extends FeedbackPanel {

	public AppFeedbackPanel(String id) {
		super(id);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message) {
		String cssClass;
		switch (message.getLevel())
		{
			case FeedbackMessage.UNDEFINED:
				cssClass = "alert alert-warning";
				break;
			case FeedbackMessage.DEBUG:
				cssClass = "alert alert-info";
				break;
			case FeedbackMessage.INFO:
				cssClass = "alert alert-info";
				break;
			case FeedbackMessage.SUCCESS:
				cssClass = "alert alert-success";
				break;
			case FeedbackMessage.WARNING:
				cssClass = "alert alert-warning";
				break;
			case FeedbackMessage.ERROR:
				cssClass = "alert alert-danger";
				break;
			case FeedbackMessage.FATAL:
				cssClass = "alert alert-danger";
				break;
			default:
				cssClass = "alert alert-danger";
		}
		return cssClass;
	}


}
