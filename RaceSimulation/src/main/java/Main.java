import akka.actor.typed.ActorSystem;

public class Main {
    public static void main(String[] args) {
        ActorSystem<RaceController.Command> actorSystem = ActorSystem.create(RaceController.create(), "RaceSimulatorSystem");
        actorSystem.tell(new RaceController.StartCommand());
    }
}
