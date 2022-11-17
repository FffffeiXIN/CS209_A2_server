package thread;

import Manager.RoomManager;
import entity.GameRoom;
//import entity.Result;

import java.io.*;
import java.net.Socket;

public class GameThread extends Thread {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = -1;
    private static final int EMPTY = 0;
    private static boolean TURN = true;
    private static final int[][] chessBoard = new int[3][3];
    private boolean win = false;
    String p1;
    String p2;
    Socket splayer1;
    Socket splayer2;
    public GameRoom gameRoom;
    public RoomManager roomManager;
    boolean state = false;

    //先setPlayer再run
    public void setPlayer(Socket player1, Socket player2, String p1, String p2) {
        this.splayer1 = player1;
        this.splayer2 = player2;
        this.p1 = p1;
        this.p2 = p2;
        state = true;
    }


    @Override
    public void run() {
        //告诉双方游戏开始 前端初始化游戏界面
        try {
            OutputStream os1 = splayer1.getOutputStream();
//            OutputStream os2 = splayer2.getOutputStream();
//            String send_str1 = "gameStart player1";
//            String send_str2 = "gameStart player2";
//            byte[] send_bytes1 = send_str1.getBytes();
//            byte[] send_bytes2 = send_str2.getBytes();
//            os1.write(send_bytes1);
//            os1.flush();
//            os2.write(send_bytes2);
//            os2.flush();

            String start_mess = "ok to start";
            byte[] send_bytes1 = start_mess.getBytes();
            os1 = splayer1.getOutputStream();
            os1.write(send_bytes1);
            os1.flush();
//            Result init = new Result(chessBoard,false,200,p1,p2);
//            ObjectOutputStream oos1 = new ObjectOutputStream(os1);
//            oos1.writeObject(init);
//            oos1.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                InputStream is1 = splayer1.getInputStream();
                byte[] buf1 = new byte[1024];
                int readLen = 0;
                readLen = is1.read(buf1);
                String action_str = new String(buf1, 0, readLen);

                //处理数据 拿到 x y
                int x = Integer.parseInt(action_str.split(" ")[0]);
                int y = Integer.parseInt(action_str.split(" ")[1]);
                chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
                win = checkWin(x, y);
                TURN = !TURN;
//                Result res = new Result(chessBoard, win, 200, p1, p2);
                //返回报文格式：是否获胜 双方都发
                String is_win = win? "Yes ":"No ";
                is_win += x +" "+y;
                byte[] send_win = is_win.getBytes();
                OutputStream os1 = splayer1.getOutputStream();
                os1.write(send_win);
                os1.flush();
//                splayer1.shutdownOutput();
                OutputStream os2 = splayer2.getOutputStream();
                os2.write(send_win);
                os2.flush();
//                splayer2.shutdownOutput();



//                serialize(res);

                if (win) break;//游戏结束

                InputStream is2 = splayer2.getInputStream();
                byte[] buf2 = new byte[1024];
                int readLen2 = 0;
                readLen2 = is2.read(buf2);
                String action_str2 = new String(buf2, 0, readLen2);

                //处理数据 拿到 x y
                int x2 = Integer.parseInt(action_str2.split(" ")[0]);
                int y2 = Integer.parseInt(action_str2.split(" ")[1]);
                chessBoard[x2][y2] = TURN ? PLAY_1 : PLAY_2;
                win = checkWin(x2, y2);
                TURN = !TURN;
//                Result res2 = new Result(chessBoard, win, 200, p1, p2);
//                serialize(res2);
                is_win = win? "Yes ":"No ";
                is_win += x2 +" "+y2;
                send_win = is_win.getBytes();
                os1 = splayer1.getOutputStream();
                os1.write(send_win);
                os1.flush();
//                splayer1.shutdownOutput();
                os2 = splayer2.getOutputStream();
                os2.write(send_win);
                os2.flush();
//                splayer2.shutdownOutput();

                if (win) break;//游戏结束

//
//
//
//                os = socket.getOutputStream();
//                pw = new PrintWriter(os);
//                pw.write("服务器欢迎你");
//
//                pw.flush();
//                splayer1.shutdownInput();
//                splayer2.shutdownInput();
            } catch (Exception e) {
                // TODO: handle exception
//            } finally {
//                //关闭资源
//                try {
//                    if (splayer1 != null)
//                        splayer1.close();
//                    if (splayer2 != null)
//                        splayer2.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
        try {
            if (splayer1 != null)
                splayer1.close();
            if (splayer2 != null)
                splayer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        state = false;
        roomManager.deleteRoom(gameRoom);
    }

    public boolean getThreadState() {
        return state;
    }

    public boolean checkWin(int x, int y) {
        int piece = chessBoard[x][y];
        if (chessBoard[x][0] == chessBoard[x][1] && chessBoard[x][1] == chessBoard[x][2]) return true;
        if (chessBoard[0][y] == chessBoard[1][y] && chessBoard[1][y] == chessBoard[2][y]) return true;
        if (x == 1 && y == 1) {
            if (chessBoard[0][0] == piece && chessBoard[2][2] == piece) return true;
            if (chessBoard[0][2] == piece && chessBoard[2][0] == piece) return true;
        }
        return false;
    }

    private boolean refreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
//            drawChess();
            return true;
        }
        return false;
    }

//    public void serialize(Serializable obj) throws Exception {
//        if (obj != null) {
//            OutputStream os1 = null;
//            OutputStream os2 = null;
//            ObjectOutputStream oos1 = null;
//            ObjectOutputStream oos2 = null;
//            try {
//                os1 = this.splayer1.getOutputStream();
//                os2 = this.splayer2.getOutputStream();
//                oos1 = new ObjectOutputStream(os1);
//                oos2 = new ObjectOutputStream(os2);
//                oos1.writeObject(obj);
//                oos1.flush();
//                oos2.writeObject(obj);
//                oos2.flush();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new Exception("序列化对象失败：" + String.valueOf(obj));
//            } finally {
//                try {
//                    if (os1 != null) {
//                        os1.close();
//                    }
//                    if (oos1 != null) {
//                        oos1.close();
//                    }
//                    if (os2 != null) {
//                        os2.close();
//                    }
//                    if (oos2 != null) {
//                        oos2.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    public static String bytesToString(byte[] bytes) {
//        //转换成base64
//        return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
//    }
//
//    public static byte[] stringToByte(String str) throws DecoderException {
//        //转换成base64
//        return org.apache.commons.codec.binary.Base64.decodeBase64(str);
//    }
//
//    public static <T extends Serializable> T deserialize(String str) throws Exception{
//        if(StringUtils.isNotEmpty(str)){
//            ByteArrayInputStream bai=null;
//            ObjectInputStream ois=null;
//            try{
//                bai=new ByteArrayInputStream(stringToByte(str));
//                ois=new ObjectInputStream(bai);
//                return (T)ois.readObject();
//            }catch (Exception e){
//                e.printStackTrace();
//                throw new Exception("字符串反序列化对象失败："+String.valueOf(str));
//            }finally {
//                try {
//                    if(ois!=null){
//                        ois.close();
//                    }
//                    if(bai!=null){
//                        bai.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

}