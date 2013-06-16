package server;

import ca.Area;
import ca.CellSpace;
import java.rmi.Naming;
import computing.node.interfaces.RemoteNodeInterface;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrewman
 */
//Klasa odzwierciedla póki co serwer.
public class ServerController {

    private CellSpace cellSpace;
    private ArrayList<RemoteNodeInterface> currentNodesList = new ArrayList<RemoteNodeInterface>();
    private ArrayList<String> hostNames = new ArrayList<String>();

    public ServerController(CellSpace space) {
        this.cellSpace = space;
    }

    public ArrayList<RemoteNodeInterface> getCurrentNodesList() {
        return currentNodesList;
    }

    //Wypisanie
    public void writeSpace() {
        for (int i = 0; i < cellSpace.getWidth(); i++) {
            for (int j = 0; j < cellSpace.getHeight(); j++) {
                for (int k = 0; k < 1; k++) {
                    System.out.print(cellSpace.getValue(i, j, k) + " ");
                }
            }
            System.out.println();
        }
    }

    // Zapis obszaru do głównej przestrzeni automatów
    public void writeAreaToCellSpace(Area area) {
        for (int i = 0; i < this.cellSpace.getWidth(); i++) {
            int ai = 1;
            for (int j = area.getStartHeight(); j < area.getEndHeight(); j++) {
                for (int k = 0; k < this.cellSpace.getDepth(); k++) {
                    this.cellSpace.setValue(i, j, k, area.getValue(i, ai, k));
                }
                ai++;
            }
        }
    }

    public boolean createRmiRegistry() throws RemoteException {
        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException ex) {
            ex.getStackTrace();
            return false;
        }
        return true;
    }

    public boolean bindRemoteNodes() {
        currentNodesList.clear();
        for (String host : hostNames) {

            try {
                String[] names = Naming.list(String.format("rmi://%s:1099", host));
                for (String name : names) {
                    currentNodesList.add((RemoteNodeInterface) Naming.lookup(name));
                }
            } catch (RemoteException remoteException) {
            } catch (MalformedURLException malformedURLException) {
            } catch (NotBoundException notBoundException) {
            }

        }
        return true;
    }

    public void clearCurrentNodes() {
        currentNodesList.clear();
    }

    public ArrayList<String> getNodesNames() {
        ArrayList<String> names = new ArrayList<String>();

        try {
            for (RemoteNodeInterface node : currentNodesList) {
                names.add(node.getNodeName());
            }
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }
        return names;
    }

    public ArrayList<String> getHostsNames() {
        return hostNames;
    }

    public void addHost(String address) {
        if (!hostNames.contains(address)) {
            hostNames.add(address);
        }
    }

    public boolean makeRemoteCall() {

        ArrayList<Area> areas = new ArrayList<Area>();

        int i = 0;
        int nodesCount = currentNodesList.size();
        int spaceHeight = this.cellSpace.getHeight();
        int part = spaceHeight / nodesCount;
        final ArrayList<Area> tmpAreas = areas;
        ArrayList<Thread> threadsList = new ArrayList<Thread>();
        try {
            
            for (RemoteNodeInterface node : currentNodesList) {

                final RemoteNodeInterface tmpNode = node;
                final CellSpace tmpCellSpace = cellSpace;
                final int tmpPart = part;
                System.out.println("i na poczatku funkcji: "+i);
                final int tmpI = i;
                
                final int tmpNodesCount = nodesCount;
                final int tmpSpaceHeight = spaceHeight;
               // ArrayList<Thread> threadsList = new ArrayList<Thread>();

                //   if (i != nodesCount - 1) {
                Thread thr =new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (tmpI != tmpNodesCount - 1) {
                                tmpAreas.add((Area) tmpNode.computeIteration(new Area(tmpCellSpace, tmpPart * tmpI, (tmpPart * (tmpI + 1)))));
                                 System.out.println("if: tmpI: "+tmpI);
                            } else {
                                tmpAreas.add((Area) tmpNode.computeIteration(new Area(tmpCellSpace, tmpPart * tmpI, tmpSpaceHeight)));
                             System.out.println("else: tmpI: "+tmpI);
                            }

                            //  tmpAreas.add((Area) tmpNode.computeIteration(new Area(tmpCellSpace, tmpPart * tmpI, (tmpPart * (tmpI + 1)))));
                        } catch (RemoteException ex) {
                            System.out.println("Wewnetrzny Remote Exception "+ex.getMessage());
                          //  Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                });
                threadsList.add(thr);
                
                i++; 
                 System.out.println("i na koncu funkcji: "+i);
            }
            
            

        } catch (Exception remoteException) {
            System.out.println("Zostal zucony Exception"+remoteException.getMessage());
            //remoteException.printStackTrace();
           // return false;
        }

        
          for (Thread thread : threadsList) 
          {
                    System.out.println("Rozmiar listy watkow: "+threadsList.size());
                    thread.start();
          }

          for (Thread thread : threadsList) {
               System.out.println("Rozmiar listy watkow w joinie"+threadsList.size());
           
               try {
                thread.join();
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted Exception: "+ex.getMessage());
                        }
          }

           for (Area area : tmpAreas) {
            writeAreaToCellSpace(area);
        }

        return true;
    }
}