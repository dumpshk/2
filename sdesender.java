import java.net.*;
import java.io.*;

class Main {

    // Permutations
    static int[] P10 = {3,5,2,7,4,10,1,9,8,6};
    static int[] P8  = {6,3,7,4,8,5,10,9};
    static int[] IP  = {2,6,3,1,4,8,5,7};
    static int[] IP1 = {4,1,3,5,7,2,8,6};
    static int[] EP  = {4,1,2,3,2,3,4,1};
    static int[] P4  = {2,4,3,1};

    // S-Boxes
    static int[][] S0 = {
        {1,0,3,2},
        {3,2,1,0},
        {0,2,1,3},
        {3,1,3,2}
    };

    static int[][] S1 = {
        {0,1,2,3},
        {2,0,1,3},
        {3,0,1,0},
        {2,1,0,3}
    };

    static int[] K1, K2;

    // Permutation function
    static int[] perm(int a[], int p[]){
        int r[] = new int[p.length];
        for(int i=0;i<p.length;i++) r[i] = a[p[i]-1];
        return r;
    }

    // Left shift
    static int[] leftShift(int[] arr, int n){
        int[] res = arr.clone();
        for(int i=0;i<n;i++){
            int first = res[0];
            for(int j=0;j<res.length-1;j++)
                res[j] = res[j+1];
            res[res.length-1] = first;
        }
        return res;
    }

    // Key generation
    static void generateKeys(int key[]){
        int[] p10 = perm(key, P10);

        int left[] = new int[5];
        int right[] = new int[5];

        System.arraycopy(p10, 0, left, 0, 5);
        System.arraycopy(p10, 5, right, 0, 5);

        // LS-1
        left = leftShift(left,1);
        right = leftShift(right,1);

        int[] merged = new int[10];
        System.arraycopy(left,0,merged,0,5);
        System.arraycopy(right,0,merged,5,5);

        K1 = perm(merged, P8);

        // LS-2
        left = leftShift(left,2);
        right = leftShift(right,2);

        System.arraycopy(left,0,merged,0,5);
        System.arraycopy(right,0,merged,5,5);

        K2 = perm(merged, P8);
    }

    // fk function
    static int[] fk(int in[], int k[]){
        int l[] = {in[0], in[1], in[2], in[3]};
        int r[] = {in[4], in[5], in[6], in[7]};

        int[] ep = perm(r, EP);

        for(int i=0;i<8;i++) ep[i] ^= k[i];

        int row = ep[0]*2 + ep[3];
        int col = ep[1]*2 + ep[2];
        int val = S0[row][col];
        int s0[] = {val/2, val%2};

        row = ep[4]*2 + ep[7];
        col = ep[5]*2 + ep[6];
        val = S1[row][col];
        int s1[] = {val/2, val%2};

        int[] p4 = perm(new int[]{s0[0],s0[1],s1[0],s1[1]}, P4);

        for(int i=0;i<4;i++) l[i] ^= p4[i];

        return new int[]{l[0],l[1],l[2],l[3],r[0],r[1],r[2],r[3]};
    }

    public static void main(String[] args) throws Exception {

        Socket s = new Socket("localhost", 9200);
        DataOutputStream o = new DataOutputStream(s.getOutputStream());
        int key[] = {1,0,1,0,0,0,0,0,1,0};

        generateKeys(key);

        int p[] = {1,0,1,0,0,1,0,1};

        int t[] = fk(perm(p, IP), K1);
        int t2[] = {t[4],t[5],t[6],t[7],t[0],t[1],t[2],t[3]};

        int ct[] = perm(fk(t2, K2), IP1);

        StringBuilder sb = new StringBuilder();
        for(int x:ct) sb.append(x);

        System.out.println("Cipher Text: " + sb.toString());

        o.writeUTF(sb.toString());
    }
}
