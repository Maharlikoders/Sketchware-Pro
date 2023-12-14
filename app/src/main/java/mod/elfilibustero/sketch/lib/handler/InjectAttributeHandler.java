package mod.elfilibustero.sketch.lib.handler;

import android.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.besome.sketch.beans.ViewBean;

import mod.elfilibustero.sketch.beans.InjectAttributeBean;
import mod.jbk.util.LogUtil;

public class InjectAttributeHandler {

    public static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("([a-zA-Z_:][a-zA-Z\\d_\\.-]*)\\s*\"([^\"]*)\"");

	private ViewBean viewBean;

	public InjectAttributeHandler(ViewBean viewBean) {
		this.viewBean = viewBean;
	}

	public String getAttributeValueOf(String name) {
		for (Pair<String, String> pair : getAttributes()) {
			if (pair.first.equals(name)) {
				return pair.second;
			}
		}
		return "";
	}

    public List<InjectAttributeBean> getInjectAttributes() {
        List<InjectAttributeBean> attributes = new ArrayList<>();
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(viewBean.inject);

        while(matcher.find()) {
            String attribute = matcher.group(1);
            String value = matcher.group(2);
            String[] attributeParts = attribute.split(":");
            String res = attributeParts.length > 1 ? attributeParts[0] : "";
            String attr = attributeParts.length > 1 ? attributeParts[1] : attribute;

            attributes.add(new InjectAttributeBean(res, attr, value));
        }
        return attributes;
    }

	public Set<Pair<String, String>> getAttributes() {
        Set<Pair<String, String>> attributePairs = new HashSet<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader("<tag xmlns:android=\"http://schemas.android.com/apk/res/android\" " +
                    "xmlns:app=\"http://schemas.android.com/apk/res-auto\" " +
                    "xmlns:tools=\"http://schemas.android.com/tools\"" +
                    viewBean.inject + "></tag>"));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                    	String name = parser.getAttributeName(i);
                    	String value = parser.getAttributeValue(i);
                    	attributePairs.add(new Pair<>(name, value));
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException | RuntimeException e) {
            LogUtil.e("InjectAttributeHandler", "Failed to parse inject property of View " + viewBean.id, e);
        }

        return attributePairs;
    }
}