import com.todo.BasicCrud
import io.vertx.core.Vertx

class StartVertx {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx()
        vertx.deployVerticle(new BasicCrud())
    }
}
