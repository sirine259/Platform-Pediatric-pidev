export const environment = {
  production: false,
  // Kubernetes/Ingress: on appelle le backend via le même domaine en utilisant des chemins relatifs (/api/...)
  // (pour Angular, si fileReplacements ne remplace pas environment.ts, ce paramétrage doit aussi être correct)
  apiUrl: 'http://localhost:8091'
};
