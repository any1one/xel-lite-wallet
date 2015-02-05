package nxt.http;


import nxt.Account;
import nxt.NxtException;
import nxt.Poll;
import nxt.db.DbIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public class GetPolls extends APIServlet.APIRequestHandler {

    static final GetPolls instance = new GetPolls();

    private GetPolls() {
        super(new APITag[]{APITag.ACCOUNTS, APITag.VS}, "includeVoters", "account", "firstIndex", "lastIndex");
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) throws NxtException {
        Account account = ParameterParser.getAccount(req, false);
        int firstIndex = ParameterParser.getFirstIndex(req);
        int lastIndex = ParameterParser.getLastIndex(req);
        boolean includeVoters = ParameterParser.getBoolean(req, "includeVoters", false);

        DbIterator<Poll> polls;

        if(account == null) {
            polls = Poll.getAllPolls(firstIndex, lastIndex);
        } else {
            polls = Poll.getPollsByAccount(account.getId(), firstIndex, lastIndex);
        }

        JSONArray pollsJson = new JSONArray();
        while (polls.hasNext()) {
            pollsJson.add(JSONData.poll(polls.next(), includeVoters));
        }

        JSONObject response = new JSONObject();
        response.put("polls", pollsJson);
        return response;
    }
}
