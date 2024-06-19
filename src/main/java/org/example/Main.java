package org.example;


import com.assemblyai.api.RealtimeTranscriber;
import javax.sound.sampled.*;
import java.io.IOException;
import static java.lang.Thread.interrupted;

public class Main {
    public static void main(String[] args) throws IOException {
        Thread thread = new Thread(() -> {
            try{
                RealtimeTranscriber  realtimeTranscriber = RealtimeTranscriber.builder()
                        .apiKey("c2e6b601dbb0428ea6094ab729cdce36")
                        .sampleRate(16_000)
                        .onSessionBegins( sessionBegins -> System.out.println("Session open ID: " + sessionBegins.getSessionId()))

                        .disablePartialTranscripts()
                        .endUtteranceSilenceThreshold(700)
                        .onFinalTranscript(transcript -> System.out.println("Final: " + transcript.getText()))
                        .onError(err -> System.out.println("Error: "+err.getMessage()))
                        .build();
                System.out.println("Connecting to Real time Transcriber");
                realtimeTranscriber.connect();

                System.out.println("Start Recording");
                AudioFormat format = new AudioFormat(16_000,16,1,true,false);

                TargetDataLine line = AudioSystem.getTargetDataLine(format);
                line.open(format);
                byte[] data = new byte[line.getBufferSize()];
                line.start();
                while (!interrupted()) {

                    line.read(data, 0, data.length);
                    realtimeTranscriber.sendAudio(data);
                }

                System.out.println("Stopping recording");
                line.close();

                System.out.println("Closing real-time transcript connection");
                realtimeTranscriber.close();

            }
            catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();

        System.out.println("Press ENTER key to stop...");
        System.in.read();
        thread.interrupt();
        System.exit(0);

    }
}