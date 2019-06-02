package br.com.catlangos.eventando.fragmentos;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.evento.Evento;
import br.com.catlangos.eventando.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class QrcodeFragment extends Fragment implements View.OnClickListener {

    private Button btnGerar;
    private ImageView imgQrcode;
    private FirebaseDatabase fireBaseDatabase;
    private DatabaseReference dataBase;
    private FirebaseUser usuario;
    private List<Evento> eventos = new ArrayList<>();

    private void recuperarListaEventos() {
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        String uid = usuario.getUid();

        Query query = dataBase.child("/evento_user/").equalTo(uid);
        final List<String> idsEventos = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idsEventos.add(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qrcode, container, false);

        recuperarListaEventos();
        inicializarComponentes(v);
        clickButton();

        List<Evento> eventos = new ArrayList<>();

        Evento e1 = new Evento();
        e1.setNome("Teste");

        Evento e2 = new Evento();
        e2.setNome("Teste2");

        eventos.add(e1);
        eventos.add(e2);


        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MeuAdaptador(eventos));

        return v;
    }

    private void clickButton() {
        btnGerar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGerar:
                gerarQrcode();
                break;
        }
    }

    private void gerarQrcode() {
        String texto = "Alterar para nome do evento + nome do usuario + id do evento + data de geração do ingresso";
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(texto, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgQrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void inicializarComponentes(View v) {
       btnGerar = v.findViewById(R.id.btnGerar);
       imgQrcode = v.findViewById(R.id.imgQrcode);
    }
}
