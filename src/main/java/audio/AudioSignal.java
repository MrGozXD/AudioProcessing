package audio;
import javax.sound.sampled.*;


/** A container for an audio signal backed by a double buffer so as to allow floating point calculation
 for signal processing and avoid saturation effects. Samples are 16 bit wide in this implementation. **/

public class AudioSignal {

    private double[] sampleBuffer;  // floating point representation of audio samples
    private double dBLevel;         // current signal level
    private int frameSize;
    
    
    /** Construct an AudioSignal that may contain up to frameSize samples.
     * @param frameSize the number of number of samples in one audio frame
     */
    public AudioSignal(int frameSize) {
        sampleBuffer = new double[frameSize];
        this.frameSize = frameSize;
    }

    /** Sets the content of this signal from another signal.
     * @param other other.length must not be lower than the length of this signal.
     */
    public void setFrom(AudioSignal other) {
        System.arraycopy(other.sampleBuffer, 0, sampleBuffer, 0, sampleBuffer.length);
    }

    /** Fills the buffer content from the given input. Byte's are converted on the fly to double's.
     * @return false if at end of stream
     */
    public boolean recordFrom(TargetDataLine audioInput) {
        byte[] byteBuffer = new byte[sampleBuffer.length * 2]; // 16 bit samples (used to  store information before using write)
        if (audioInput.read(byteBuffer, 0, byteBuffer.length) == -1) return false;
           
        for (int i = 0; i < sampleBuffer.length; i++) {
                int sample = 0;
                sample |= byteBuffer[2 * i] & 0xFF; // low byte
                sample |= byteBuffer[2 * i + 1] << 8; // high byte
                sampleBuffer[i] = sample / 32768.0; // signed 16 bit
            }
        /** dbLevel = update signal level in dB here 
         * Method 1 : Do it yourself
         * Method 2 : Use the method getdLevel() from javax.sound.sampled.DataLine
         * https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/DataLine.html
         */
        
        /**
        double sum = 0;
        for (int i = 0; i < sampleBuffer.length; i++) {
            sum += sampleBuffer[i] * sampleBuffer[i];
        }
        dBLevel = 10 * Math.log10(sum / sampleBuffer.length);
        */

        dBLevel=audioInput.getLevel();
        return true; 
    }

    /** Plays the buffer content to the given output.
     * @return false if at the end of stream
     */
    public boolean playTo(SourceDataLine audioOutput) {
        byte[] byteBuffer = new byte[sampleBuffer.length * 2]; // 16 bit samples
        for (int i = 0; i < sampleBuffer.length; i++) {
            int sample = (int) (sampleBuffer[i] * 32768.0); // signed 16 bit
            byteBuffer[2 * i] = (byte) (sample & 0xFF); // low byte
            byteBuffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF); // high byte
        }
        if (audioOutput.write(byteBuffer, 0, byteBuffer.length) == -1) return false;
        return true;
    }

    /** Getter & Setter */

    public double getSample(int i) {
        return sampleBuffer[i];
    }

    public void setSample(int i, double value) {
        sampleBuffer[i] = value;
    }

    public double getdBLevel() {
        return dBLevel;
    }

    public int getFrameSize() {
        return frameSize;
    }
    
    /**
    Complex[] computeFFT() {
        Complex[] complexSignal = new Complex[sampleBuffer.length];
        for (int i = 0; i < sampleBuffer.length; i++) {
            complexSignal[i] = new Complex(sampleBuffer[i], 0);
        }
        return FFT.fft(complexSignal);
    }
    */
    

    public static void main(String args[]) {
        /** Define the audio format */

        AudioFormat format = new AudioFormat(8000, 16, 1, true, true);
        
        try {
            TargetDataLine tLine = AudioSystem.getTargetDataLine(format);
            tLine.open();
            tLine.start();

            AudioSignal myAudio = new AudioSignal(32000);
            myAudio.recordFrom(tLine);

            /** Print the audio signal to test */
            for (int i=0; i<myAudio.getFrameSize(); i++){
                System.out.print(myAudio.getSample(i));
                System.out.print(" ");
            }
            System.out.println("");

            SourceDataLine sLine = AudioSystem.getSourceDataLine(format);
            sLine.open();
            sLine.start();

            myAudio.playTo(sLine);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
