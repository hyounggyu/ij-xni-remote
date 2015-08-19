import ij.IJ;
import ij.plugin.PlugIn;

public class XNI_Remote implements PlugIn {
    @Override
    public void run(String arg) {
        IJ.showMessage("This would be our plugin!");
    }
    public static void main(final String... args) {
        new ij.ImageJ();
        new XNI_Remote().run("");
    }
}