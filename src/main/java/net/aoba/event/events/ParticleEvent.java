/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.ParticleListener;
import net.minecraft.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;

public class ParticleEvent extends AbstractEvent {
    private final ParticleEffect particleEffect;

    public ParticleEvent(ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
    }

    public ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            ParticleListener particleListener = (ParticleListener) listener;
            particleListener.onParticle(this);
        }
    }

    @Override
    public Class<ParticleListener> GetListenerClassType() {
        return ParticleListener.class;
    }
}
