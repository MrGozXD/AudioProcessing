package audio;

import javax.sound.sampled.*;

/** The main audio processing class, implemented as a Runnable so
 * as to be run in a separated execution Thread.
 */

public class AudioProcessor implements Runnable {

    private AudioSignal inputSignal, outputSignal;
    private TargetDataLine audioInput;
    private SourceDataLine audioOutput;
    private boolean isThreadRunning = false; //makes it possible to terminate the thread

    /** Creates an AudioProcessor that takes input from the given TargetDataLine, and plays back
     * to the given SourceDataLine.
     * @param frameSize the size of the audio buffer. The shorter, the lower the latency.
     */
    public AudioProcessor(TargetDataLine audioInput, SourceDataLine audioOutput, int frameSize) {
        this.audioInput = audioInput;
        this.audioOutput = audioOutput;
        inputSignal = new AudioSignal(frameSize);
        outputSignal = new AudioSignal(frameSize);
    }

    /** Audio processing thread code. Basically an infinite loop that continuously fills the sample
     * buffer with audio data fed by a TargetDataLine, and then applies some audio effect, if any,
     * and finally copies data back to a SourceDataLine.
     */

     @Override
    public void run() {
        isThreadRunning = true;
        while (isThreadRunning) {
            if (!inputSignal.recordFrom(audioInput)) break;
            // TODO : apply audio effect here
            outputSignal.setFrom(inputSignal);
            if (!outputSignal.playTo(audioOutput)) break;
        }
        audioInput.close();
        audioOutput.close();
    }

    /** Tells the thread loop to break as soon as possible. This is an asynchronous process. */
    public void terminateAudioThread() {
        isThreadRunning = false;
    }

    /** Getter & Setter */

    /** Returns the current input signal. */
    public AudioSignal getInputSignal() {
        return inputSignal;
    }

    /** Returns the current output signal. */

    public AudioSignal getOutputSignal() {
        return outputSignal;
    }

    /** Returns the current audio input. */

    public TargetDataLine getAudioInput() {
        return audioInput;
    }

    /** Returns the current audio output. */

    public SourceDataLine getAudioOutput() {
        return audioOutput;
    }


    /* an example of a possible test code */

    public static void main(String[] args) throws LineUnavailableException {
        TargetDataLine inLine = AudioIO.obtainAudioInput("Default Audio Device", 16000);
        SourceDataLine outLine = AudioIO.obtainAudioOutput("Default Audio Device", 16000);
        AudioProcessor myAudioProcessor = new AudioProcessor(inLine, outLine, 1024);
        inLine.open();
        outLine.open();
        inLine.start();
        outLine.start();
        new Thread(myAudioProcessor).start();
        System.out.println("A new Audio thread has been created!");
    }

}
