# Foriba Bulut e-Fatura API Java Test Projesi

Bu proje Foriba Bulut e-Fatura API web servis metodlarının nasıl kullanılması gerektiği ile ilgili örnek olması için oluşturulmuştur. Proje yalnızca 
test sisteminde çalışmakta ve web servislere bağlantı ayarları da projede bulunmaktadır.

 **e-Fatura Ürünü İçin:**

- Kullanıcı listesinin indirilmesi.
- Belge gönderimi.
- Gelen giden belge listesi alımı.
- Yüklenen belgenin indirilmesi.
- Gelen ve giden belgelerin pdf,html,xslt alınabilir.
- Yüklenen zarfların statülerinin sorgulanması. 


işlemleri yapılmaktadır.

Web servis erişim güvenliği basic authentication ile sağlanmaktadır. Web servisleri kullanacak istemcilerin Foriba Bulut e-Fatura Portal test sistemi
kullanıcı adı ve şifresine sahip olmaları gerekmektedir. Bu kullanıcı adı ve şifre ile web service doğrulaması gerçekleştirilebilir.


## Kurulum

Bu proje Eclipse geliştirme ortamında Java yazılım dili standartları ile oluşturulmuştur.

ForibaClientEInvoiceJar projesinin test edilmesi:

- İndirilen proje File ->İmport ->Maven -> Existing Maven Projects  üzerinden import edilir.
- sendUBL methodu için örnek envInvoice.xml,Invoice.xml,envAR.xml fatura verileri src/test.java package altında bulunmaktadır.Sender,Receiver ve bütün vkntckn alanları düzenlenerek kullanılmalıdır.Doldurulması gereken alanlar xml'ler içerisinde belirtilmiştir.
- Foriba Bulut e-Fatura Portal test sistemi kullanıcı adı ve şifresi, resources altındaki user.properties içerisine username ve password alanlarına girilmelidir.
- Application.java classında main methodunda içerisinde bulunan WS methodlarını kullanmak için gerekli bilgiler girilerek getRawUserList,getInvResponses,sendUBL,getUBL,getUBLList,getEnvelopeStatus,getInvoiceView WS methodları test edilebilir.



# Lisans
  
Foriba Bulut API Java Test Projesi, **Foriba R&D** ekibi tarafından API kullanımını anlatmak için hazırlanmıştır, izinsiz olarak ticari uygulamalarda kullanılması yasaktır.  
