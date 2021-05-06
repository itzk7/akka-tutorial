import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class WorkerBehaviour extends AbstractBehavior<WorkerBehaviour.Command> {
    public static class Command implements Serializable {
        public static final long serialVersionUID = 1L;
        private final String message;
        private final ActorRef<ManagerBehaviour.Command> actorRef;

        public Command(String message, ActorRef<ManagerBehaviour.Command> actorRef) {
            this.message = message;
            this.actorRef = actorRef;
        }

        public String getMessage() {
            return message;
        }

        public ActorRef<ManagerBehaviour.Command> getActorRef() {
            return actorRef;
        }
    }
    public WorkerBehaviour(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(WorkerBehaviour :: new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(command -> {
                    if(command.getMessage().equals("start")) {
                        command.getActorRef().tell(new ManagerBehaviour.ResultCommand(
                                new BigInteger(2000, new Random()).nextProbablePrime()));
                    }
                    return this;
                })

                .build();
    }
}
