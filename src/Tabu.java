import java.io.*;
import java.util.*;
public class Tabu {
    int []vex;              //存储顶点信息,下标就是顶点号,顶点数从1开始，整数表示着色的是颜色几
    int [][]edge;           //边,1表示存在该边，0表示不存在,默认为0
    int vexnum,edgenum;     //顶点数和边数
    int k;                  //至少需要的颜色数
    int [][]p;              //记录颜色的冲突问题，横坐标表示该顶点，纵坐标表示颜色冲突度是多少
    int conflict=0;         //临时保存各个顶点颜色冲突数
    int flag;               //判断是否所有顶点颜色冲突都为0，默认flag=0表示不存在顶点冲突，大于0表示顶点几有冲突
    int co=0;               //用于flagnum函数中有冲突顶点时，表示该顶点的其他颜色冲突数为0的颜色
    int [][]ta;             //存储顶点颜色禁忌次数
    int it=0;               //统计迭代次数
    int v,a;                //记录禁忌的顶点颜色改为颜色a的全局变量
    Random r=new Random();
    void init() throws Exception {                     //将数据传入
        String s1;
        String []s2;
        int a,b;
        FileReader reader=new FileReader("D:\\example\\3.txt");
        BufferedReader br=new BufferedReader(reader);
        s1=br.readLine();
        s2=s1.split(" ");
        vexnum=Integer.parseInt(s2[2]);
        edgenum=Integer.parseInt(s2[3]);
        vex=new int[vexnum+1];
        edge=new int[vexnum+1][vexnum+1];
        while((s1=br.readLine())!=null) {
            s2=s1.split(" ");
            a=Integer.parseInt(s2[1]);
            b=Integer.parseInt(s2[2]);
            edge[a][b]=1;
            edge[b][a]=1;
        }
        k=vexnum;
        p=new int[vexnum+1][k+1];
        ta=new int[vexnum+1][k+1];
        br.close();
    }
    void initcolor() {                          //对顶点颜色进行初始化
        for(int i=1;i<=vexnum;i++){
            vex[i]=r.nextInt(k)+1;              //各个顶点随机生成颜色1到颜色vexnum
        }
        for(int i=1;i<=vexnum;i++) {            //i表示顶点，t表示顶点i的颜色假设为颜色几，j表示与i相邻的顶点,初始化颜色冲突数
            for(int t=1;t<=k;t++) {
                for(int j=1;j<=vexnum;j++) {
                    if(vex[j]==t&&edge[i][j]==1) {
                        conflict++;
                    }
                }
                p[i][t]=conflict;                   //顶点i颜色t的冲突数
                conflict=0;
            }
        }
    }
    void color_reduce() {
        for(int i=1;i<=vexnum;i++) {
            if(vex[i]==k+1){                         //将顶点颜色为k+1的颜色随机为颜色【1，k】之一
                vex[i]=r.nextInt(k)+1;
                for(int j = 1; j <=vexnum; j++) {
                    if(edge[i][j]==1){
                        p[j][vex[i]]++;
                        it++;
                    }
                }
            }
        }
    }
    int flagnum(){               //对flag的赋值函数
        int h=0,i,x=0;
        int d=0;
        for(i=1;i<=vexnum;i++) {
            if(p[i][vex[i]]==0) {
                continue;
            }
            else {
                for(h=1;h<=k;h++) {
                    if(p[i][h]==0&&ta[i][h]<=it) {
                        x=1;
                        d=i;
                        co=h;
                        break;
                    }
                }
            }
            if(x==1) {              //此时已经记录了co(冲突顶点的未染的颜色且该颜色冲突数为0)的值以及d(冲突的顶点)的值
                break;
            }
        }
        if(i==vexnum+1) {
            if(h==k+1) {           //表示颜色数k+1达到最少
                return -1;
            }
        }
        return d;                 //返回0或者返回一个冲突的顶点
    }
    void tabu_init(){             //随机找到一个非禁忌点
        int i,j;
        for(i=1;i<=vexnum;i++) {
            for(j=1;j<=k;j++) {
                if(vex[i]!=j&&ta[v][j]<=it){
                    v=i;
                    a=j;
                    break;
                }
            }
            if(j<=k)
                break;
        }
    }
    void tabu_search(){                        //找到冲突数较小的非禁忌点
        int best_count = 0;
        for(int i=1;i<=vexnum;i++) {
            for(int j=1;j<=k;j++) {
                if(vex[i]!=j&&p[v][a]>p[i][j]&&ta[i][j]<=it){
                    v=i;
                    a=j;
                    best_count = 1;
                }
                else if(vex[i]!=j&&p[v][a]==p[i][j]&&ta[i][j]<=it) {
                    best_count++;
                    int x=r.nextInt(best_count);
                    if(x==0) {
                        v=i;
                        a=j;
                    }
                }
            }
        }
        ta[v][vex[v]]=it+r.nextInt(10)+30;       //禁忌次数为30-49的随机数
        for (int j = 1; j <=vexnum ; j++) {
            if(edge[v][j]==1){
                p[j][vex[v]]--;
                p[j][a]++;
            }
        }
        vex[v]=a;
        it++;
    }
    void init_ta() {
        v=1;
        a=1;
        for(int i=1;i<=vexnum;i++) {
            for(int j=1;j<=k;j++) {
                ta[i][j]=0;
            }
        }
    }
    void reduce_conflict(){
        for(int i=1;i<=vexnum;i++)
            if(p[i][vex[i]]>0){
                int con=p[i][vex[i]];
                int col=vex[i];
                for(int j=1;j<=k;j++){
                    if(j!=col&&con>p[i][j]){
                        con=p[i][j];
                        col=j;
                    }
                }
                if(col!=vex[i]){
                    for(int j=1;j<=vexnum;j++){
                        if(edge[i][j]==1){
                            it++;
                            p[j][col]++;
                            p[j][vex[i]]--;
                        }
                    }
                    vex[i]=col;
                }
            }
    }
    void color_change(int f){            //修改相邻的顶点颜色冲突数
        int i=f;                        //顶点i的颜色可以修改
        for(int j=1;j<=vexnum;j++){
            if(edge[i][j]==1){
                p[j][co]++;
                p[j][vex[i]]--;
            }
        }
        vex[i]=co;
        it++;
    }
    void ksize() throws Exception {
        init();
        init_ta();
        initcolor();
        int size=0;
        boolean b=true;
        while(k>0) {
            it++;
            flag=flagnum();
            if(flag==0) {                         //表示不存在顶点冲突，颜色数可以减一
                if(size!=0) {                     //表示局部最优后将顶点颜色随机涂色后找到了更优的解则size初始为0
                    size=0;
                    init_ta();
                }
//              b=true;
                k--;
                System.out.println(k);
                color_reduce();
            }
            else if(flag==-1) {
//                if(b==true){
//                    reduce_conflict();
//                    b=false;
//                }

                tabu_init();
                tabu_search();
                size++;
                if(size==100000)
                    break;
            }
            else{                       //表示存在顶点颜色冲突，需要修改颜色,即flag>0
                color_change(flag);
            }
        }
    }
    public static void main(String []args) throws Exception {
        Tabu D=new Tabu();
        D.ksize();
        System.out.print("需要颜色的种类数:");
        System.out.println(D.k+1);
        System.out.println(D.it);
    }
}