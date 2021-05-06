import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;

public class Main {
//    public static void main(String[] args) {
//        // wrapper for the first actors, the first actor is actor system
//        ActorSystem<String> firstSimpleActorSystem = ActorSystem.create(FirstSimpleBehaviour.create()
//                , "FirstSimpleActorSystem");
//        firstSimpleActorSystem.tell("id please?");
//        firstSimpleActorSystem.tell("create a child");
//        firstSimpleActorSystem.tell("id please?");
//        // Now the system will always open we need to shut it down
//    }

    public static void main(String[] args) {
        ActorSystem<ManagerBehaviour.Command> actorSystem = ActorSystem.create(ManagerBehaviour.create(), "BigPrimes");
        actorSystem.tell(new ManagerBehaviour.InstructionCommand("start"));
    }
}
