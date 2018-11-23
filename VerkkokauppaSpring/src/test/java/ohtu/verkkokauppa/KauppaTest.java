package ohtu.verkkokauppa;

import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.*;

public class KauppaTest {
    
    @Before
    public void setUp() {
    
    }
	
	@Test
	public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
		// luodaan ensin mock-oliot
		Pankki pankki = mock(Pankki.class);
		
		Viitegeneraattori viite = mock(Viitegeneraattori.class);
		// määritellään että viitegeneraattori palauttaa viitten 42
		when(viite.uusi()).thenReturn(42);

		Varasto varasto = mock(Varasto.class);
		// määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
		when(varasto.saldo(1)).thenReturn(10); 
		when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

		// sitten testattava kauppa 
		Kauppa k = new Kauppa(varasto, pankki, viite);              

		// tehdään ostokset
		k.aloitaAsiointi();
		k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
		k.tilimaksu("pekka", "12345");

		// sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
		verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(),anyInt());   
		// toistaiseksi ei välitetty kutsussa käytetyistä parametreista
	}	   
	
 	@Test
	public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaParametreilla() {
		Pankki pankki = mock(Pankki.class);

		Viitegeneraattori viite = mock(Viitegeneraattori.class);

		when(viite.uusi()).thenReturn(42);
		
		Varasto varasto = mock(Varasto.class);
		
		when(varasto.saldo(1)).thenReturn(10);
		when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

		Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "1234");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("1234"), anyString(), eq(5));
	}
    
    @Test
	public void ostotapahtumaKahdellaEriTuotteella() {
		Pankki pankki = mock(Pankki.class);

		Viitegeneraattori viite = mock(Viitegeneraattori.class);

		when(viite.uusi()).thenReturn(42);
		
		Varasto varasto = mock(Varasto.class);
		
		when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(10);
		when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sokeri", 2));

		Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "1234");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("1234"), anyString(), eq(7));
	}

    @Test
	public void ostotapahtumaKahdellaSamallaTuotteella() {
		Pankki pankki = mock(Pankki.class);

		Viitegeneraattori viite = mock(Viitegeneraattori.class);

		when(viite.uusi()).thenReturn(42);
		
		Varasto varasto = mock(Varasto.class);
		
		when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(10);
		when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

		Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "1234");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("1234"), anyString(), eq(10));
	}

    @Test
	public void ostotapahtumaTuotteelleJokaOnLoppuJaTuotteelleVarastossa() {
		Pankki pankki = mock(Pankki.class);

		Viitegeneraattori viite = mock(Viitegeneraattori.class);

		when(viite.uusi()).thenReturn(42);
		
		Varasto varasto = mock(Varasto.class);
		
		when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(0);
		when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sokeri", 2));

		Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "1234");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("1234"), anyString(), eq(5));
	}
    
    @Test
    public void aloitaAsiointiNollaaMaksutiedot() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);

        k.tilimaksu("pekka", "1234");

        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("1234"), anyString(), eq(5));

    }
    
    @Test
    public void uusiViiteNumeroJokaiselleTapahtumalle() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("petteri", "4321");

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "1234");
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("heikki", "1243");
       
        verify(viite, times(3)).uusi();
    }
    
    @Test
    public void poistaminenKoristaPalauttaaVarastoon() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);

        when(viite.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.poistaKorista(1);
       
        verify(varasto, times(1)).palautaVarastoon(new Tuote(1, "maito", 5));
    }
}
