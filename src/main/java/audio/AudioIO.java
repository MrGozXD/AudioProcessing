package audio;

import javax.sound.sampled.*;
import java.util.Arrays;

/** A collection of static utilities related to the audio system */
public class AudioIO {

    /** Displays every audio mixer available on the current system */
    public static void printAudioMixers() {
        System.out.println("Available audio mixers:");
        Arrays.stream(AudioSystem.getMixerInfo())
        .forEach(e->System.out.println("- name=\""+e.getName()+"\" vendor=\""+e.getVendor()+"\" description=\""+e.getDescription()+"\" version=\""+e.getVersion()+"\""));
    }

    /** @return a Mixer.Info whose name matches the given string.
     * Example of use : getMixerInfo("Macbook default output"); */
    
    public static Mixer.Info getMixerInfo(String mixerName) {
        return Arrays.stream(AudioSystem.getMixerInfo())
        .filter(e->e.getName().equalsIgnoreCase(mixerName))
        .findFirst()
        .get();
    }

    /**FOR UI: Get a list of names of available mixers */
    public static String[] getAudioMixersNames() {
        return Arrays.stream(AudioSystem.getMixerInfo())
        .map(e->e.getName())
        .toArray(String[]::new);
    }

    /** Return a line that's appropriate for recording sound from a microphone.
     * Example of use :
     * TargetDataLine line = obtainInputLine("USB Audio Device", 8000);
     * @param mixerName a string that matches one of the available mixers.
     * @see AudioSystem.getMixerInfo() which provides a list of all mixers on your system.
     */

    public static TargetDataLine obtainAudioInput(String mixerName, int sampleRate) throws LineUnavailableException {
        Mixer.Info mixerInfo = getMixerInfo(mixerName);
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true); // 16-bit mono signed little-endian
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) mixer.getLine(info);
        line.open(format);
        return line;
    }

     /** Return a line that's appropriate for playing sound to a loudspeaker */

    public static SourceDataLine obtainAudioOutput(String mixerName, int sampleRate) throws LineUnavailableException {
        Mixer.Info mixerInfo = getMixerInfo(mixerName);
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true); // 16-bit mono signed little-endian
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) mixer.getLine(info);
        line.open(format);
        return line;
    }

    public static AudioProcessor startAudioProcessing(String inputMixer, String outputMixer, int sampleRate, int frameSize) throws LineUnavailableException {
        TargetDataLine inputLine = obtainAudioInput(inputMixer, sampleRate);
        SourceDataLine outputLine = obtainAudioOutput(outputMixer, sampleRate);
        AudioProcessor processor = new AudioProcessor(inputLine, outputLine, frameSize);
        inputLine.open();
        outputLine.open();
        inputLine.start();
        outputLine.start();
        new Thread(processor).start();
        return processor;
    }

    public static void stopAudioProcessing(AudioProcessor processor) {
        processor.terminateAudioThread();
    }

    public static void main(String[] args) {
        printAudioMixers();

        /** List
        Available audio mixers:
        - name="Port Haut-parleurs (Realtek High Def" vendor="Unknown Vendor" description="Port Mixer" version="10.0"
        - name="Port Microphone (Realtek High Defini" vendor="Unknown Vendor" description="Port Mixer" version="10.0"
        - name="Périphérique audio principal" vendor="Unknown Vendor" description="Direct Audio Device: DirectSound Playback" version="Unknown Version"
        - name="Haut-parleurs (Realtek High Definition Audio)" vendor="Unknown Vendor" description="Direct Audio Device: DirectSound Playback" version="Unknown Version"
        - name="Pilote de capture audio principal" vendor="Unknown Vendor" description="Direct Audio Device: DirectSound Capture" version="Unknown Version"
        - name="Microphone (Realtek High Defini" vendor="Unknown Vendor" description="Direct Audio Device: DirectSound Capture" version="Unknown Version"
        */

        try{
            AudioProcessor processor = startAudioProcessing("Microphone (Realtek High Defini", "Haut-parleurs (Realtek High Definition Audio)", 8000, 1024);
            Thread.sleep(5000);
            stopAudioProcessing(processor);
        } catch (Exception e) {
            e.printStackTrace();
        }


        
    }
}
