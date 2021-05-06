
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class FirstSimpleBehaviour extends AbstractBehavior<String> {
    private FirstSimpleBehaviour(ActorContext<String> context) {
        super(context);
    }

    public static Behavior<String> create() {
        return Behaviors.setup(FirstSimpleBehaviour :: new);
    }
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("id please?", () -> {
                    // path is the url of the actor
                    /*
                     * akka://FirstSimpleActorSystem/user -> First actor path
                     * akka://FirstSimpleActorSystem/user/secondActor -> Second actor path
                     * */
                    System.out.println(getContext().getSelf().path());
                    return this;
                })
                .onMessageEquals("create a child", () -> {
                    ActorRef<String> secondActor = getContext().spawn(FirstSimpleBehaviour.create(), "secondActor");
                    secondActor.tell("id please?");
                    secondActor.tell("id please?");
                    return this;
                })
                // Receiver will receive the message to process
                .onAnyMessage(message -> {
                    System.out.println("I received the message " + message);
                    return this;
                })
                .build();
    }
}
