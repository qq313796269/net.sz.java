/**
 * 特别鸣谢修仙项目组负责人吴章义
 */
package net.sz.game.engine.navmesh.main;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.navmesh.path.NavMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Keith
 */
public class MapWindow {

    private static final Logger log = Logger.getLogger(MapWindow.class);

    static JPanel p = new JPanel();

    protected JFrame frame;
    protected ViewPane view;
    protected volatile boolean keepRunning = true;
    protected boolean pause = false;

    protected final Object mutex = new Object();
    protected ArrayList<AWTEvent> events = new ArrayList<>();
    protected ArrayList<AWTEvent> eventsCopy = new ArrayList<>();
    protected Vector3 curMouseMovePoint = new Vector3();

    protected Random rand = new Random(11);
    protected Vector3 curPoint;
    protected JTextField jtx;
    protected JFileChooser chooser;
    protected String choosertitle;
    protected Player player;
    protected Main main;

    public MapWindow(Main main) {
        this.main = main;
        main.setMapWindow(this);
        this.view = createViePane();
        frame = new JFrame(this.getClass().getSimpleName());
//        double width = Toolkit.getDefaultToolkit().getScreenSize().width - 300; //得到当前屏幕分辨率的高
//        double height = Toolkit.getDefaultToolkit().getScreenSize().height - 200;//得到当前屏幕分辨率的宽
        frame.setSize((int) (this.view.getPlayer().map.getWidth() * this.view.getPlayer().map.getScale()) + 10,
                (int) (this.view.getPlayer().map.getHeight() * this.view.getPlayer().map.getScale()) + 55);//设置大小
        frame.setLocation(350, 50); //设置窗体居中显示
        frame.setResizable(false);//禁用最大化按钮
        frame.add(view);

        MenuBar menuBar = new MenuBar();

        Menu menu = new Menu();
        menu.setLabel("菜单");
        MenuItem file = new MenuItem();
        file.setLabel("加载寻路文件");
        menu.add(file);

        file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result;
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle(choosertitle);
                log.error("---" + choosertitle);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(p) == JFileChooser.APPROVE_OPTION) {
//                    log.error("getCurrentDirectory(): "
//                            + chooser.getCurrentDirectory());
//                    log.error("getSelectedFile() : "
//                            + chooser.getSelectedFile());
                    loadMap(chooser.getSelectedFile().getAbsolutePath());
                } else {
                    log.error("No Selection ");
                }
            }
        });

        menuBar.add(menu);
        frame.setMenuBar(menuBar);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                keepRunning = false;
                System.exit(0);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }
        });
        frame.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }

            public void keyReleased(KeyEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }

            public void keyTyped(KeyEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }
        });
        view.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }

            public void mouseClicked(MouseEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }

            public void mouseEntered(MouseEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }

            public void mouseExited(MouseEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }
        });
        view.addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }

            public void mouseDragged(MouseEvent e) {
                synchronized (mutex) {
                    events.add(e);
                }
            }
        });

        init();

        frame.setVisible(true);

        Thread gameLoopThread = new Thread("GameLoop") {
            public void run() {
                long lastUpdateNanos = System.nanoTime();
                while (keepRunning) {
                    long currentNanos = System.nanoTime();
                    float seconds = (currentNanos - lastUpdateNanos) / 1000000000f;
                    processEvents();
                    if (pause != true) {
                        update(seconds);
                    }
                    view.render();
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                    lastUpdateNanos = currentNanos;
                }
            }
        };
        gameLoopThread.setDaemon(false);
        gameLoopThread.start();
    }

    private NavMap loadMap(String filePath) {
        NavMap map = null;
        try {
            map = new NavMap(filePath, true);
            if (map != null && player != null) {
                player.setMap(map);
                frame.setSize((int) (map.getWidth() * map.getScale()) + 10, (int) (map.getHeight() * map.getScale()) + 55);
            }
//            if (view!=null) {
//                view.setBounds((int)(-map.getStartX()*map.getScale()), (int)(-map.getStartZ()*map.getScale())
//                        , (int)(map.getWidth()*map.getScale()), (int)(map.getHeight()*map.getScale()));
//            }
            return map;
        } catch (Exception e) {
            log.error("Map:" + e.toString());
        }
        return null;
    }

    private ViewPane createViePane() {
        NavMap map = null;
        try {
            File file = new File(System.getProperty("user.dir"));
            String path = null;
            if ("target".equals(file.getName())) {
                path = file.getPath();
            } else if ("Nav_build".equals(file.getName())) {
                path = file.getPath();
            } else {
                path = file.getPath() + File.separatorChar + "target";
            }
            map = loadMap(path + File.separatorChar + "100.navmesh");
        } catch (Exception e) {
            log.error("Map:" + e.toString());
        }
        player = new Player(map);
        return new ViewPane(player);
    }

    public void init() {
        view.getPlayer().setPos(new Vector3(400, 280));
        view.getPlayer().setTarget(view.getPlayer().getPos().copy());
    }

    public void randMove() {
        Vector3 t = view.getMap().getRandomPointInPaths(view.getPlayer().pos, 80, 2);
        if (t != null) {
            view.getPlayer().target.x = t.x;
            view.getPlayer().target.z = t.z;
            view.getPlayer().path();
        }
    }

    public void processEvents() {
        synchronized (mutex) {
            if (events.size() > 0) {
                eventsCopy.addAll(events);
                events.clear();
            }
        }
        if (eventsCopy.size() > 0) {
            for (int i = 0; i < eventsCopy.size(); i++) {
                AWTEvent awtEvent = eventsCopy.get(i);
                if (awtEvent instanceof MouseEvent) {
                    MouseEvent e = (MouseEvent) awtEvent;
                    switch (e.getID()) {
                        case MouseEvent.MOUSE_MOVED:
                            break;
                        case MouseEvent.MOUSE_PRESSED:
                            curMouseMovePoint.x = e.getX();
                            curMouseMovePoint.z = e.getY();
                            break;
                        case MouseEvent.MOUSE_DRAGGED:
                            Vector3 p = view.getMap().getPointInPaths(e.getX(), e.getY());
                            if (p != null) {
                                view.getPlayer().target.x = e.getX();
                                view.getPlayer().target.z = e.getY();
                                view.getPlayer().path();
                            }
                            break;
                        case MouseEvent.MOUSE_CLICKED:
                            /*有缩放*/
                            main.setPosition(new Vector3(e.getX() / view.getMap().getScale(), e.getY() / view.getMap().getScale()));
                            view.getPlayer().pos.x = e.getX();
                            view.getPlayer().pos.z = e.getY();
                            break;
                        default:
                            break;
                    }
                } else if (awtEvent instanceof java.awt.event.KeyEvent) {
                    KeyEvent e = (KeyEvent) awtEvent;
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        if (e.getKeyCode() == KeyEvent.VK_R) {
                            this.init();
                        }
                        if (e.getKeyCode() == KeyEvent.VK_P) {
                            if (pause == true) {
                                pause = false;
                            } else {
                                pause = true;
                            }
                        }
                    }
                } else if (awtEvent instanceof ComponentEvent) {
                    ComponentEvent e = (ComponentEvent) awtEvent;
                    if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
                        this.init();
                    }
                }
            }
            eventsCopy.clear();
        }
    }

    double totalSeconds = 0;

    public void update(float seconds) {
        view.getPlayer().update(seconds);
        totalSeconds += seconds;
    }

    /**
     * @return the frame
     */
    public JFrame getFrame() {
        return frame;
    }
}
