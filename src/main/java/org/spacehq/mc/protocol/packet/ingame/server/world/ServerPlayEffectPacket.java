// 
// Decompiled by Procyon v0.5.30
// 

package org.spacehq.mc.protocol.packet.ingame.server.world;

import org.spacehq.packetlib.io.NetOutput;
import java.io.IOException;
import org.spacehq.mc.protocol.data.game.values.world.effect.HardLandingEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.BreakPotionEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.BreakBlockEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.SmokeEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.RecordEffectData;
import org.spacehq.mc.protocol.util.NetUtil;
import org.spacehq.mc.protocol.data.game.values.world.effect.SoundEffect;
import org.spacehq.mc.protocol.data.game.values.MagicValues;
import org.spacehq.mc.protocol.data.game.values.world.effect.ParticleEffect;
import org.spacehq.packetlib.io.NetInput;
import org.spacehq.mc.protocol.data.game.values.world.effect.WorldEffectData;
import org.spacehq.mc.protocol.data.game.Position;
import org.spacehq.mc.protocol.data.game.values.world.effect.WorldEffect;
import org.spacehq.packetlib.packet.Packet;

public class ServerPlayEffectPacket implements Packet
{
    private WorldEffect effect;
    private Position position;
    private WorldEffectData data;
    private boolean broadcast;
    
    private ServerPlayEffectPacket() {
    }
    
    public ServerPlayEffectPacket(final WorldEffect effect, final Position position, final WorldEffectData data) {
        this(effect, position, data, false);
    }
    
    public ServerPlayEffectPacket(final WorldEffect effect, final Position position, final WorldEffectData data, final boolean broadcast) {
        this.effect = effect;
        this.position = position;
        this.data = data;
        this.broadcast = broadcast;
    }
    
    public WorldEffect getEffect() {
        return this.effect;
    }
    
    public Position getPosition() {
        return this.position;
    }
    
    public WorldEffectData getData() {
        return this.data;
    }
    
    public boolean getBroadcast() {
        return this.broadcast;
    }
    
    @Override
    public void read(final NetInput in) throws IOException {
        final int id = in.readInt();
        if (id >= 2000) {
            this.effect = MagicValues.key(ParticleEffect.class, id);
        }
        else {
            this.effect = MagicValues.key(SoundEffect.class, id);
        }
        this.position = NetUtil.readPosition(in);
        final int value = in.readInt();
        if (this.effect == SoundEffect.PLAY_RECORD) {
            this.data = new RecordEffectData(value);
        }
        else if (this.effect == ParticleEffect.SMOKE) {
            this.data = MagicValues.key(SmokeEffectData.class, value);
        }
        else if (this.effect == ParticleEffect.BREAK_BLOCK) {
            this.data = new BreakBlockEffectData(value);
        }
        else if (this.effect == ParticleEffect.BREAK_SPLASH_POTION) {
            this.data = new BreakPotionEffectData(value);
        }
        else if (this.effect == ParticleEffect.HARD_LANDING_DUST) {
            this.data = new HardLandingEffectData(value);
        }
        this.broadcast = in.readBoolean();
    }
    
    @Override
    public void write(final NetOutput out) throws IOException {
        int id = 0;
        if (this.effect instanceof ParticleEffect) {
            id = MagicValues.value(Integer.class, (Enum<?>)this.effect);
        }
        else if (this.effect instanceof SoundEffect) {
            id = MagicValues.value(Integer.class, (Enum<?>)this.effect);
        }
        out.writeInt(id);
        NetUtil.writePosition(out, this.position);
        int value = 0;
        if (this.data instanceof RecordEffectData) {
            value = ((RecordEffectData)this.data).getRecordId();
        }
        else if (this.data instanceof SmokeEffectData) {
            value = MagicValues.value(Integer.class, (Enum<?>)this.data);
        }
        else if (this.data instanceof BreakBlockEffectData) {
            value = ((BreakBlockEffectData)this.data).getBlockId();
        }
        else if (this.data instanceof BreakPotionEffectData) {
            value = ((BreakPotionEffectData)this.data).getPotionId();
        }
        else if (this.data instanceof HardLandingEffectData) {
            value = ((HardLandingEffectData)this.data).getDamagingDistance();
        }
        out.writeInt(value);
        out.writeBoolean(this.broadcast);
    }
    
    @Override
    public boolean isPriority() {
        return false;
    }
}
