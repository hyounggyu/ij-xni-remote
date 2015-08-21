import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class XNI_Remote implements PlugIn {

    public ImagePlus getImagePlus(String host) throws IOException {
        Socket s = new Socket(host, 5051);

        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        DataInputStream in = new DataInputStream(s.getInputStream());

        out.write(0); // Send hello

        byte dt = in.readByte();
        int z = in.readInt(); // Shape 0: frame
        int h = in.readInt(); // Shape 1: height
        int w = in.readInt(); // Shape 2: width

        ImageStack stack = new ImageStack(w, h);
        IJ.showProgress(0, z);
        for (int i = 0; i < z; i++) {
            float[] data = new float[h*w];
            for (int j = 0; j < h*w; j++) {
                data[j] = in.readFloat();
            }
            stack.addSlice("", data);
            IJ.showProgress(i + 1, z);
        }

        return new ImagePlus("stack", stack);
    }

    @Override
    public void run(String arg) {
        String host="127.0.0.1";
        GenericDialog gd = new GenericDialog("Fetch Image Data");
        gd.addStringField("Host:", host);
        gd.showDialog();
        if (gd.wasCanceled()) return;
        host = gd.getNextString();
        try {
            ImagePlus image = getImagePlus(host);
            image.show();
        } catch (IOException e) {
            IJ.showMessage("Connection Error");
        }
    }

    public static void main(final String... args) {
        new ij.ImageJ();
        new XNI_Remote().run("");
    }
}
