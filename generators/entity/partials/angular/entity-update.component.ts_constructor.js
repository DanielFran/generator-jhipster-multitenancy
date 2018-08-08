const tmpl = (context) => {
    const template = `private activatedRoute: ActivatedRoute, private ${context.tenantNameLowerFirst}Service: ${context.tenantNameUpperFirst}Service, private principal: Principal) {
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
    }`;

    return template;
};

module.exports = {
    tmpl
};
