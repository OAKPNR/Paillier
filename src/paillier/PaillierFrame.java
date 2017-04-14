package paillier;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class PaillierFrame extends javax.swing.JFrame {    

    public PaillierFrame() {
        initComponents();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ENCRYPT = new javax.swing.JButton();
        lblPlain = new javax.swing.JLabel();
        lblCypher = new javax.swing.JLabel();
        DECRYPT = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        encText = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        decText = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ENCRYPT.setText("ENCRYPT");
        ENCRYPT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ENCRYPTActionPerformed(evt);
            }
        });

        lblPlain.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPlain.setText("PLAIN TEXT");

        lblCypher.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCypher.setText("ENCRYPTED TEXT");

        DECRYPT.setText("DECRYPT");
        DECRYPT.setActionCommand("");
        DECRYPT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DECRYPTActionPerformed(evt);
            }
        });

        encText.setColumns(20);
        encText.setLineWrap(true);
        encText.setRows(5);
        jScrollPane1.setViewportView(encText);

        decText.setColumns(20);
        decText.setLineWrap(true);
        decText.setRows(5);
        jScrollPane2.setViewportView(decText);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPlain)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCypher)
                                .addGap(0, 292, Short.MAX_VALUE))
                            .addComponent(jScrollPane2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(DECRYPT, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ENCRYPT, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblPlain)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(ENCRYPT, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(40, 40, 40)
                .addComponent(lblCypher)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(DECRYPT, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    
    //BigInteger sınıfından asal sayı oluşturulurken kabul edilebilri hata payıdır
    //(1 - 1/2^certainty) şeklinde hesaplanır
    //Bu değeri düşük tutmamız asal olma olasılığını arttırır
    private static final int CERTAINTY = 64;      
    private static int modSize;                  
    private static BigInteger p,q,lambda,n,n2,g,Mu;
    
    String plainText,decryptText;
    BigInteger plainBigInt,cipherPlain,decPlain;
    
    public static void setModSize(int aModSize) {
            modSize = aModSize;
            generateKeys();
    }
    
    public static void generateKeys()
    {
        // rastgele p anahtarı üretilir
        p = new BigInteger(modSize / 2, CERTAINTY, new SecureRandom());     
        
        do
        {   // p den farklı rastgele q anahtarı üretilir
            q = new BigInteger(modSize / 2, CERTAINTY, new SecureRandom()); 
        }
        while (q.compareTo(p) == 0);

        // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
        lambda = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))).divide(
                p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));
        
        n = p.multiply(q);             
        n2 = n.multiply(n);       
        
        do
        {
            // Z*n2 aralığında rastgele g anahtarı üretildi
            g = rndZN(n2,2);
        }
        while (g.modPow(lambda, n2).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1);
        
        // mu = (L(g^lambda mod n^2))^{-1} mod n
        Mu = g.modPow(lambda, n2).subtract(BigInteger.ONE).divide(n).modInverse(n);
    }
    
     // Belirlenen uzayda rastgele r değeri üretir
    public static BigInteger rndZN(BigInteger n,int a)
    {
        BigInteger r;        
        do
        {r = new BigInteger(modSize*a, new SecureRandom());}
        while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);
        
        return r;
    }
    
     public BigInteger encrypt(BigInteger m)
    {
        // Z*n kümesinde rastgele r değeri hesaplanır
        BigInteger r = rndZN(n,1);
        
        //"c = g^m * r^n mod n^2" fonksiyonu şifreli metini hesaplar
        return (g.modPow(m, n2).multiply(r.modPow(n, n2))).mod(n2);
    }
     
    public BigInteger decrypt(BigInteger c)
    {     
        // "m = L(c^lambda mod n^2) * mu mod n" fonksiyonu ile mesaj çözülüyor 
        // L(u) = (u-1)/n fonksiyon
        return c.modPow(lambda, n2).subtract(BigInteger.ONE).divide(n).multiply(Mu).mod(n);
    }
    
    public static void printValues()
    {
        System.out.println("p:       " + p);
        System.out.println("q:       " + q);
        System.out.println("lambda:  " + lambda);
        System.out.println("n:       " + n);
        System.out.println("nsquare: " + n2);
        System.out.println("g:       " + g);
        System.out.println("mu:      " + Mu);
    }
        
    //Metin Şifreleme
    private void ENCRYPTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ENCRYPTActionPerformed
        if (encText.getText().length()==0) {
            JOptionPane.showMessageDialog(null, "BOŞ GEÇİLEMEZ");
        }         
        
        plainText=encText.getText();              
        plainBigInt=new BigInteger(plainText.getBytes());  //Metini BigInteger tipine dönüştürür
        cipherPlain=encrypt(plainBigInt);                  //Metinin şifrelenmesi
                
        decText.setText(cipherPlain+"");
        lblCypher.setText("ENCRYPTED TEXT");          printValues();     
    }//GEN-LAST:event_ENCRYPTActionPerformed

    
    //Metin Şifre Çözme
    private void DECRYPTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DECRYPTActionPerformed
        try{
        decPlain=decrypt(cipherPlain);  
        }
        catch(Exception e){
           JOptionPane.showMessageDialog(null, "Önce Şifreleme Yapınız");
        }
        decryptText=new String(decPlain.toByteArray());
        decText.setText(decryptText);
        lblCypher.setText("DECRYPTED TEXT");
    }//GEN-LAST:event_DECRYPTActionPerformed
    
    public static void main(String args[]) {
        setModSize(2048);
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PaillierFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PaillierFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PaillierFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PaillierFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PaillierFrame().setVisible(true);
            }
        });
        
        
 
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DECRYPT;
    private javax.swing.JButton ENCRYPT;
    private javax.swing.JTextArea decText;
    private javax.swing.JTextArea encText;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCypher;
    private javax.swing.JLabel lblPlain;
    // End of variables declaration//GEN-END:variables
}
