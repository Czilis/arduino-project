package uz.embeddedsystems.arduino_client.client;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ManageAddressesFragment extends Fragment {

    @Bind(R.id.btn_add_address)
    Button btnAddAddress;
    @Bind(R.id.txt_ip_address)
    EditText txtIpAddress;
    @Bind(R.id.txt_port)
    EditText txtPort;
    private boolean ipInserted = false;
    private boolean portInserted = false;
    private Listener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manage_addresses, container, false);
        ButterKnife.bind(this, view);
        setWatchers();
        txtIpAddress.setText("");
        txtPort.setText("");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    public void setListener(final Listener listener){
        this.listener = listener;
    }

    @OnClick(R.id.btn_add_address)
    public void onButtonAddAddressClicked() {
        final Set<String> ipSet = SharedPreferencesUtils.getSavedPair(getActivity()).first;
        final Set<String> portSet = SharedPreferencesUtils.getSavedPair(getActivity()).second;

        ipSet.add(txtIpAddress.getText().toString());
        portSet.add(txtPort.getText().toString());

        SharedPreferencesUtils.savePair(getActivity(), new Pair(ipSet, portSet));
        listener.onAddressAdded();
    }


    private void setWatchers() {
        txtIpAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 7) {
                    ipInserted = true;
                    checkIfButtonCanChangeState();
                } else {
                    ipInserted = false;
                    checkIfButtonCanChangeState();
                }
            }
        });

        txtPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 4) {
                    portInserted = true;
                    checkIfButtonCanChangeState();
                } else {
                    portInserted = false;
                    checkIfButtonCanChangeState();
                }
            }
        });
    }

    private void checkIfButtonCanChangeState() {
        if (ipInserted && portInserted) {
            btnAddAddress.setEnabled(true);
        } else {
            btnAddAddress.setEnabled(false);
        }
    }

    public interface Listener {
        void onAddressAdded();
    }

}
