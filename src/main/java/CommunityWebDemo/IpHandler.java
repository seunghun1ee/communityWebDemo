package CommunityWebDemo;

public class IpHandler {
    public IpHandler() {

    }

    public String trimIpAddress(String ip) {
        String[] strings = ip.split("\\.");
        return strings[0] + "." + strings[1] + ".***.***";
    }
}
