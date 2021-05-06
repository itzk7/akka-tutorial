import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.awt.image.RasterOp;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RaceController extends AbstractBehavior<RaceController.Command> {

    private Object TIMER_KEY;
    public static class StartCommand implements Command {
        private static final long serialVersionUID = 1L;
    }
    private static class GetPositionsCommand implements Command {
        private static final long serialVersionUID = 1L;
    }
    public static class RacerCompleteCommand implements Command {
        private static final long serialVersionUID = 1L;
        private ActorRef<Racer.Command> racer;

        public RacerCompleteCommand(ActorRef<Racer.Command> racer) {
            this.racer = racer;
        }

        public ActorRef<Racer.Command> getRacer() {
            return racer;
        }
    }
    public static class RacerUpdateCommand implements Command {
        private static final long serialVersionUID = 1L;
        private final ActorRef<Racer.Command> racer;
        private final int position;

        public RacerUpdateCommand(ActorRef<Racer.Command> racer, int position) {
            this.racer = racer;
            this.position = position;
        }

        public ActorRef<Racer.Command> getRacer() {
            return racer;
        }

        public int getPosition() {
            return position;
        }
    }
    private RaceController(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(RaceController::new);
    }
    private Map<ActorRef<Racer.Command>, Integer> currentPositions;
    private Map<ActorRef<Racer.Command>, Long> results;
    private long start;
    private final int raceLength = 100;
    private final int noOfRacers = 10;

    private void displayRace() {
        for (int i = 0; i < 50; ++i) System.out.println();
        System.out.println("Race has been running for " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
        int displayLength = 200;
        System.out.println("    " + new String (new char[displayLength]).replace('\0', '='));
        for (ActorRef<Racer.Command> racer: currentPositions.keySet()) {
            System.out.println(racer.path() + " : "  + new String (new char[currentPositions.get(racer) * displayLength / 100]).replace('\0', '*'));
        }
    }
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, message -> {
                    start = System.currentTimeMillis();
                    currentPositions = new HashMap<>();
                    results = new HashMap<>();
                    for (int i = 0; i < noOfRacers; i++) {
                        ActorRef<Racer.Command> racer = getContext()
                                .spawn(Racer.create(), "Racer"+i);
                        currentPositions.put(racer, 0);
                        racer.tell(new Racer.StartCommand(raceLength));
                    }

                    return Behaviors.withTimers(timers -> {
                        timers.startTimerAtFixedRate(TIMER_KEY, new GetPositionsCommand(), Duration.ofSeconds(1));
                        return this;
                    });
                })
                .onMessage(GetPositionsCommand.class, message -> {
                    for (ActorRef<Racer.Command> racer : currentPositions.keySet()) {
                        racer.tell(new Racer.PositionCommand(getContext().getSelf()));
                        displayRace();
                    }
                    return this;
                })
                .onMessage(RacerUpdateCommand.class, message -> {
                    currentPositions.put(message.getRacer(), message.getPosition());
                    return this;
                })
                .onMessage(RacerCompleteCommand.class, message -> {
                    results.put(message.getRacer(), System.currentTimeMillis());
                    if(results.size() == noOfRacers) {
                        return completeRace();
                    }
                    return Behaviors.same();
                })
                .build();
    }

    private Receive<Command> completeRace() {
        return newReceiveBuilder()
                .onMessage(GetPositionsCommand.class, message -> {
                    for(ActorRef<Racer.Command> actorRef : currentPositions.keySet()) {
                        getContext().stop(actorRef);
                    }
                    showResult();
                    return Behaviors.withTimers(timer -> {
                        timer.cancelAll();
                        return Behaviors.stopped();
                    });
                })
                .build();
    }

    void showResult() {
        System.out.println("Results");
        results.values().stream().sorted().forEach(it -> {
            for (ActorRef<Racer.Command> key : results.keySet()) {
                if (results.get(key) == it) {
                    System.out.println("Racer " + key.path().toString().substring(key.path().toString().length()-1) + " finished in " + ( (double)it - start ) / 1000 + " seconds.");
                }
            }
        });
    }
    public static interface Command extends Serializable {}
}
