import java.net.*; 
import java.io.*; 
class Main {
    static int[] IP={2,6,3,1,4,8,5,7}, IP1={4,1,3,5,7,2,8,6},
    EP={4,1,2,3,2,3,4,1}, p4={2,4,3,1};
    static int[][] S0={{1,0,3,2},{3,2,1,0},{0,2,1,3},{3,1,3,2}}; 
    static int[][] S1={{0,1,2,3},{2,0,1,3},{3,0,1,0},{2,1,0,3}}; 
    static int[] K1={1,0,1,0,0,1,0,0},K2={0,1,0,0,0,0,1,1};
    static int[] perm(int a[], int p[]){
        int r[]= new int[p.length];
        for(int i=0;i<p.length;i++) r[i]=a[p[i]-1];
        return r;
    }
    static int[] fk(int in[], int k[]){
        int l[]={in[0], in[1], in[2], in[3]};
        int r[]={in[4], in[5], in[6], in[7]};
        int[] ep=perm(r,EP);
        for(int i=0;i<8;i++) ep[i]^=k[i];
        
        int s0[]={S0[ep[0]*2+ ep[3]][ep[1]*2+ep[2]]/2,
            S0[ep[0]*2+ep[3]][ep[1]*2+ep[2]]%2
        };
        int s1[]={S1[ep[4]*2+ ep[7]][ep[5]*2+ep[6]]/2,S1[ep[4]*2+ ep[7]][ep[5]*2+ep[6]]%2};
        int P4[]= perm(new int[]{s0[0],s0[1],s1[0],s1[1]}, p4);
        for(int i=0;i<4;i++) l[i]^=P4[i];
        return new int[]{l[0],l[1],l[2],l[3],r[0],r[1],r[2],r[3]};
    }
    public static void main(String[] args) throws Exception{
        ServerSocket s= new ServerSocket(9200);
        Socket soc= s.accept();
        DataInputStream d= new DataInputStream(soc.getInputStream());
        String ss= d.readUTF();
        int[] ct= new int[8];
        int j=0;
        for(char i: ss.toCharArray()){
            ct[j++]=i-'0';
        }
        int t[]= fk(perm(ct,IP),K2);
        int sw[]={t[4],t[5],t[6],t[7],t[0],t[1],t[2],t[3]};
        int pt[]=perm(fk(sw,K1),IP1);
        StringBuilder sb= new StringBuilder();
        for(int i:pt) sb.append(i);
        System.out.println(sb.toString());
    }
}