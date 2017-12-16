import com.todo.BasicCrud
import com.todo.user.UserVerticle
import io.vertx.core.Vertx

class StartVertx {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx()
        vertx.deployVerticle(new BasicCrud())
    }
}
