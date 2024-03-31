import java.net.URLConnection;
import java.net.URL;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Caio Araujo
 */

public class HtmlAnalyzer {
    private static String deepestText = "";

    // variable set to -1 to avoid confusion with the first level 0
    private static int deepestLevel = -1;

    public static void startAnalyzer(String url) {
        // try to connect to the URL and get the HTML content
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();
            String html = new String(connection.getInputStream().readAllBytes());

            if (IsHtmlMalformed(html)) {
                throw new Exception("malformed HTML");
            } else
                Analyze(html);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Recursive function to find the deepest text in the HTML
    public static void Analyze(String html){
        findDeepestText(html, 0);
        System.out.println(deepestText);
    }

    private static void findDeepestText(String html, int level) {

        // Regular expression to match HTML tags
        String regex = "<[^>]*>([^<]*)</[^>]*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        // If a match is found, the function is called recursively
        if (matcher.find()) {
            findDeepestText(matcher.group(1), level + 1);
        }
        else if (level > deepestLevel) {
            deepestLevel = level;
            deepestText = html.trim();
        }
    }
    private static boolean IsHtmlMalformed(String html) {

        // Regular expression to match HTML tags
        String regex = "</?\\w+>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        // Stack will be responsible for keeping track of opening tags
        Stack<String> stack = new Stack<>();

        while (matcher.find()) {
            String tag = matcher.group();
            if (!tag.startsWith("</")) {
                // If this is an opening tag, it'll be pushed onto the stack
                stack.push(tag);
            } else {
                // If this is a closing tag, it'll be compared to the tag on top of the stack
                if (stack.isEmpty()) {
                    // There's a closing tag without a corresponding opening tag (malformed HTML)
                    return true;
                }
                String openingTag = stack.pop();
                if (!tag.equals(openingTag.replace("<", "</"))) {
                    // The opening and closing tags don't match (malformed HTML)
                    return true;
                }
            }
        }

        // If there are any tags left on the stack, the HTML is malformed
        return !stack.isEmpty();
    }

    public static void main(String[] args) {
        // check if length of args array is valid
        if (args.length > 0) {
            for (String url : args) {
                startAnalyzer(url);
            }
        }
        else
            System.out.println("No command line arguments found.");
    }
}
