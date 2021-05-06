import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManagerBehaviour extends AbstractBehavior<ManagerBehaviour.Command> {
    public interface Command extends Serializable {}
    public static class InstructionCommand implements Command {
        public static final long serialVersionUID = 1L;
        private final String message;

        public InstructionCommand(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
    public static class ResultCommand implements Command {
        public static final long serialVersionUID = 1L;
        private final BigInteger prime;

        public BigInteger getPrime() {
            return prime;
        }

        public ResultCommand(BigInteger prime) {
            this.prime = prime;
        }
    }
    private ManagerBehaviour(ActorContext<Command> context) {
        super(context);
    }
    private final SortedSet<BigInteger> primes = new TreeSet<>();
    public static Behavior<Command> create() {
        return Behaviors.setup(ManagerBehaviour :: new);
    }
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InstructionCommand.class, command -> {
                    if(command.getMessage().equals("start")) {
                        for (int i = 0; i < 20; i++) {
                            ActorRef<WorkerBehaviour.Command> child = getContext().spawn(WorkerBehaviour.create(), "Worker" + i);
                            child.tell(new WorkerBehaviour.Command("start", getContext().getSelf()));
                        }
                    }
                    return this;
                })
                .onMessage(ResultCommand.class, command -> {
                    primes.add(command.getPrime());
                    System.out.println(primes.size());
                    return this;
                })
                .build();
    }
}
