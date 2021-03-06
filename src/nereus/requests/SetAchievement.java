package nereus.requests;

import nereus.dispatcher.IRequest;
import nereus.dispatcher.RequestException;
import nereus.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;

public class SetAchievement implements IRequest {
   public SetAchievement() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String field = params[0];
      int index = Integer.parseInt(params[1]);
      int value = Integer.parseInt(params[2]);
      world.users.setAchievement(field, index, value, user);
   }
}
