import com.express4j.Express4J;
import com.express4j.events.Request;
import com.express4j.service.HttpRequest;
import com.express4j.service.HttpResponse;

public class ExpressTest {
	public static void main(String[] args) throws Exception {
		Express4J app = new Express4J("./www/");
		app.addStatic("/assets");
		app.get("/", new Request() {
			public void handle(HttpRequest req, HttpResponse res) throws Exception {
				res.send("test");
			}
		});
		app.get("/:test", new Request() {
			public void handle(HttpRequest req, HttpResponse res) throws Exception {
				res.send(req.getParams().getOrDefault("test", "Something went wrong!"));
			}
		});
		app.listen(8080);
		System.out.println("Application has launched!");
	}
}
