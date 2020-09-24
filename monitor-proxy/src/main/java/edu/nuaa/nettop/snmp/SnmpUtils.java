package edu.nuaa.nettop.snmp;

import edu.nuaa.nettop.vo.snmp.SnmpRequest;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 15:51
 */
@Component
public class SnmpUtils {

    /**
     * 获取指定OID对应的table值
     */
    public static List<String> walk(String oid, SnmpRequest snmpModel) {
        Snmp snmp = null;
        PDU pdu;
        CommunityTarget target;
        List<String> result = new ArrayList<>();

        String communityName = snmpModel.getCommunityName();
        String hostIp = snmpModel.getDeviceIp();
        int port = snmpModel.getPort();
        int version = snmpModel.getVersion();
        try {
            DefaultUdpTransportMapping dm = new DefaultUdpTransportMapping();
//            dm.setSocketTimeout(3000);
            snmp = new Snmp(dm);
            snmp.listen();
            target = new CommunityTarget();
            target.setCommunity(new OctetString(communityName));
            target.setVersion(version);
            target.setAddress(new UdpAddress(hostIp + "/" + port));
            target.setTimeout(1000);
            target.setRetries(1);

            TableUtils tutils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
            OID[] columns = new OID[1];
            columns[0] = new VariableBinding(new OID(oid)).getOid();
            List<TableEvent> list = tutils.getTable(target, columns, null, null);
            boolean flag = false;
            int i = 0;
            if (oid.equals(".1.3.6.1.2.1.2.2.1.10")
                    || oid.equals(".1.3.6.1.2.1.2.2.1.2")
                    || oid.equals(".1.3.6.1.2.1.2.2.1.8")
                    || oid.equals(".1.3.6.1.2.1.2.2.1.16")) {
                flag = true;
            }
            for (TableEvent e : list) {
                VariableBinding[] vb = e.getColumns();
                if (null == vb) {
                    continue;
                }
                result.add(vb[0].getVariable().toString());
                i++;
                if (flag && i == 10) {
                    break;
                }
//                System.out.println(vb[0].getVariable().toString());
            }
            snmp.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (snmp != null) {
                    snmp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
