package audio.effect;

import audio.AudioSignal;

public class Echo {

    /** echo effect */

    private int delay;
    private double decay;

    public Echo(int delay, double decay) {
        this.delay = delay;
        this.decay = decay;
    }

    public void applyEffect(AudioSignal inputSignal, AudioSignal outputSignal) {
        for (int i = 0; i < inputSignal.getFrameSize(); i++) {
            double sample = inputSignal.getSample(i);
            if (i >= delay) {
                sample += outputSignal.getSample(i - delay) * decay;
            }
            outputSignal.setSample(i, sample);
        }
    }

}
