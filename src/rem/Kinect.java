package rem;

import com.jme3.math.Vector3f;
import kinecttcpclient.KinectTCPClient;

public class Kinect {

    private KinectTCPClient client;
    private int[][][] joints = new int[6][][];
    public static final int a = 2, b = 3;

    public static enum Joint {

        HIPCENTER(0),
        SPINE(1),
        SHOULDERCENTER(2),
        HEAD(3),
        SHOULDERLEFT(4),
        ELBOWLEFT(5),
        WRISTLEFT(6),
        HANDLEFT(7),
        SHOULDERRIGHT(8),
        ELBOWRIGHT(9),
        WRISTRIGHT(10),
        HANDRIGHT(11),
        HIPLEFT(12),
        KNEELEFT(13),
        ANKLELEFT(14),
        FOOTLEFT(15),
        HIPRIGHT(16),
        KNEERIGHT(17),
        ANKLERIGHT(18),
        FOOTRIGHT(19);
        private int val;

        private Joint(int v) {
            this.val = v;
        }
    };
    private int skelecount = 0;

    public Kinect() {
        this("127.0.0.1", 8001);
    }

    public Kinect(String ipaddress, int port) {
        client = new KinectTCPClient(ipaddress, port);
    }

    private boolean isValid(int skeletonIndex, int jointIndex) {
        return joints != null
                && skeletonIndex < joints.length
                && joints[skeletonIndex] != null
                && jointIndex < joints[skeletonIndex].length
                && joints[skeletonIndex][jointIndex] != null
                && joints[skeletonIndex][jointIndex].length >= 3;
    }

    private Vector3f getPosition(int skeletonIndex, int jointIndex) {
        if (isValid(skeletonIndex, jointIndex)) {
            return new Vector3f(joints[skeletonIndex][jointIndex][1], joints[skeletonIndex][jointIndex][2], joints[skeletonIndex][jointIndex][3]);
        }
        return null;
    }

    public void update() {
        skelecount = 0;
        if (client != null) {
            int[] skeleton = client.readSkeleton();
            if (skeleton != null) {
                for (int i = 1; i <= skeleton[0]; i++) { // (skeleton.length - 17) / 149
                    joints[i - 1] = KinectTCPClient.getJointPositions(skeleton, i);
                    skelecount = i;
                }
            }
        }
    }

    public int getSkeletonCount() {
        return skelecount;
    }

    public Vector3f getJoint(int skelIndex, Joint j) {
        return getPosition(skelIndex, j.val);
    }
}
