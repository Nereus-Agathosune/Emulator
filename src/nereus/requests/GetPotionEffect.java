package nereus.requests;

import nereus.db.objects.Aura;
import nereus.db.objects.AuraEffects;
import nereus.db.objects.Skill;
import nereus.db.objects.SkillAuras;
import nereus.dispatcher.IRequest;
import nereus.dispatcher.RequestException;
import nereus.world.World;
import it.gotoandplay.smartfoxserver.data.Room;
import it.gotoandplay.smartfoxserver.data.User;
import java.util.Iterator;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetPotionEffect implements IRequest {
   public GetPotionEffect() {
      super();
   }

   public void process(String[] params, User user, World world, Room room) throws RequestException {
      String ref = params[0];
      int skillId = Integer.parseInt(params[1]);
      Skill skill = (Skill)world.skills.get(Integer.valueOf(skillId));
      if(skill == null) {
         throw new RequestException("Potion Info not found!");
      } else {
         JSONObject seia = new JSONObject();
         JSONObject o = new JSONObject();
         o.put("id", Integer.valueOf(skillId));
         o.put("ref", ref);
         o.put("nam", skill.getName());
         o.put("anim", skill.getAnimation());
         o.put("mp", Integer.valueOf(skill.getMana()));
         o.put("desc", skill.getDescription());
         o.put("range", Integer.valueOf(skill.getRange()));
         o.put("fx", skill.getEffects());
         o.put("tgt", skill.getTarget());
         o.put("typ", skill.getType());
         o.put("strl", skill.getStrl());
         o.put("cd", Integer.valueOf(skill.getCooldown()));
         Iterator multi = skill.auraskill.entries().iterator();

         while(true) {
            int auraid;
            do {
               if (!multi.hasNext()) {
                  seia.put("cmd", seia);
                  seia.put("o", o);
                  world.send(seia, user);
                  Map skills1 = (Map)user.properties.get("skills");
                  skills1.put(skill.getReference(), skillId);
                  return;
               }

               Map.Entry<Integer, SkillAuras> entry = (Map.Entry)multi.next();
               SkillAuras iniaurainfo = (SkillAuras)entry.getValue();
               auraid = iniaurainfo.auraid;
            } while(auraid <= 0);

            JSONArray skills = new JSONArray();
            Aura aura = (Aura)world.auras.get(auraid);
            if (!aura.effects.isEmpty()) {
               JSONObject auraInfo = new JSONObject();
               JSONArray effects = new JSONArray();
               Iterator i$ = aura.effects.iterator();

               while(i$.hasNext()) {
                  int effectId = (Integer)i$.next();
                  AuraEffects ae = (AuraEffects)world.effects.get(effectId);
                  JSONObject effect = new JSONObject();
                  effect.put("typ", ae.getType());
                  effect.put("sta", ae.getStat());
                  effect.put("id", ae.getId());
                  effect.put("val", ae.getValue());
                  effects.add(effect);
               }

               if (!effects.isEmpty()) {
                  auraInfo.put("e", effects);
               }

               if (aura.getDuration() > 0) {
                  auraInfo.put("t", "s");
               }

               auraInfo.put("nam", aura.getName());
               skills.add(auraInfo);
            }

            o.put("auras", skills);
         }
      }
   }
}
