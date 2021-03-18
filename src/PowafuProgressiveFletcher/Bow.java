package PowafuProgressiveFletcher;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;

public enum Bow {

    OAK_SHORT(20, 16.5,25,"Oak shortbow (u)", "Oak shortbow"),
    OAK_LONG(25, 25,30,"Oak longbow (u)", "Oak longbow"),
    WILLOW_SHORT(35,33.33,40, "Willow shortbow (u)", "Willow shortbow"),
    WILLOW_LONG(40, 41.5,55,"Willow longbow (u)", "Willow longbow"),
    MAPLE_SHORT(50,50,75,"Maple shortbow (u)", "Maple shortbow"),
    MAPLE_LONG(55,58.3,85,"Maple longbow (u)", "Maple longbow"),
    YEW_LONG(65,75,286,"Yew longbow (u)", "Yew longbow"),
    MAGIC_LONG(85, 91.5,1025,"Magic longbow (u)", "Magic longbow");

    private final int lvl;
    private final double xp;
    private final int price;
    private final String material;
    private final String product;

    Bow(int lvl, double xp, int price, String material, String product){
        this.lvl = lvl;
        this.xp = xp;
        this.price = price;
        this.material = material;
        this.product = product;
    }

    private boolean canString() { return Skills.getLevel(Skill.FLETCHING) >= lvl; }

    public double getXp(){return xp;}
    public int getPrice(){return price;}
    public String getMaterial(){return material;}
    public String getProduct(){return product;}

    public static Bow getTargetBow(){
        Bow target = OAK_SHORT;

        for (Bow b : Bow.values())
        {
            if (b.canString() && b.lvl > target.lvl)
                target = b;
        }
        return target;
    }

    public static int getTargetLvl(){
        if (Skills.getLevel(Skill.FLETCHING) >= 65)
            return 85;
        if (Skills.getLevel(Skill.FLETCHING) >= 55)
            return 65;
        if (Skills.getLevel(Skill.FLETCHING) >= 50)
            return 55;
        if (Skills.getLevel(Skill.FLETCHING) >= 40)
            return 50;
        if (Skills.getLevel(Skill.FLETCHING) >= 35)
            return 40;
        if (Skills.getLevel(Skill.FLETCHING) >= 25)
            return 35;

        return 25;
    }
}
