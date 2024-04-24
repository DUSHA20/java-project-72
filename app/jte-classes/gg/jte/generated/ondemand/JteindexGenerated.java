package gg.jte.generated.ondemand;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.html";
	public static final int[] JTE_LINE_INFO = {14,14,14,14,14,14,17,17,17,17,17,17};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor) {
		jteOutput.writeContent("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n    <title>Homepage</title>\n    <link href=\"/webjars/bootstrap/5.2.3/css/bootstrap.min.css\" rel=\"stylesheet\">\n</head>\n<body>\n<div class=\"container\">\n    <h1 class=\"mt-5\">Hello, World!</h1>\n    <p>Welcome to my website!</p>\n</div>\n\n");
		jteOutput.writeContent("\n<script src=\"/webjars/bootstrap/5.2.3/js/bootstrap.bundle.min.js\"></script>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		render(jteOutput, jteHtmlInterceptor);
	}
}
