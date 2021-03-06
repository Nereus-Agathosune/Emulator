package nereus.requests.guild;

import nereus.dispatcher.IRequest;
import nereus.dispatcher.RequestException;
import nereus.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import jdbchelper.QueryResult;
import net.sf.json.JSONObject;

public class GuildDemote implements IRequest {
   public GuildDemote() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      int guildId = ((Integer)user.properties.get("guildid")).intValue();
      int userRank = ((Integer)user.properties.get("guildrank")).intValue();
      String username = params[1].toLowerCase();
      if(guildId <= 0) {
         throw new RequestException("You do not have a guild!");
      } else if(userRank < 2) {
         throw new RequestException("Invalid /gd request.");
      } else {
         QueryResult result = world.db.jdbc.query("SELECT users.id, users_guilds.GuildID, users_guilds.Rank FROM users LEFT JOIN users_guilds ON UserID = id WHERE Name = ?", new Object[]{username});
         if(result.next()) {
            int clientGuildID = result.getInt("GuildID");
            int clientRank = result.getInt("Rank");
            int clientDbId = result.getInt("id");
            result.close();
            if(clientGuildID <= 0) {
               throw new RequestException(username + " does belong to a guild!");
            } else if(clientGuildID != guildId) {
               throw new RequestException(username + " is not in your guild!");
            } else if(clientRank >= userRank) {
               throw new RequestException("Invalid /gd request.");
            } else {
               --clientRank;
               if(clientRank >= 0 && clientRank < userRank) {
                  world.db.jdbc.run("UPDATE users_guilds SET Rank = ? WHERE UserID = ?", new Object[]{Integer.valueOf(clientRank), Integer.valueOf(clientDbId)});
                  world.sendGuildUpdate(world.users.getGuildObject(((Integer)user.properties.get("guildid")).intValue()));
                  world.sendToGuild(new String[]{"server", username + "\'s rank has been changed to " + world.users.getGuildRank(clientRank)}, (JSONObject)user.properties.get("guildobj"));
                  User client = world.zone.getUserByName(username);
                  if(client != null) {
                     client.properties.put("guildrank", Integer.valueOf(clientRank));
                  }

                  result.close();
               } else {
                  throw new RequestException("Invalid /gd request.");
               }
            }
         } else {
            result.close();
            throw new RequestException("Player \"" + username + "\" could not be found.");
         }
      }
   }
}
